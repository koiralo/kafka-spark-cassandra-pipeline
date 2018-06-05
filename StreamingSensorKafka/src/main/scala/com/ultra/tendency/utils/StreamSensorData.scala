package com.ultra.tendency.utils

import java.util.Properties

import com.ultra.tendency.domain.SensorDataRaw
import config.StreamingSettings
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession, DataFrame, SaveMode}
import org.apache.spark.sql.functions.{from_unixtime}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object StreamSensorData extends App {

  val kafkaProps = StreamingSettings.KafkaConsumer
  val cassandraProps = StreamingSettings.cassandra


  //create an entry point for spark streaming

  val spark = SparkSession
    .builder()
    .appName("view-deployer")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  @transient lazy val sc: SparkContext = {
    spark.sparkContext
  }

  @transient lazy val sqlContext: SQLContext = {
    spark.sqlContext
  }

  //Set Config for cassandra connection

  spark.conf.set("spark.cassandra.connection.host", cassandraProps.host)
  //  spark.conf.set("spark.cassandra.auth.username", cassandraProps.user)
  //  spark.conf.set("spark.cassandra.auth.password", cassandraProps.pass)


  // funtion to start streaming
  startStreamingKafka()


  def startStreamingKafka(): Unit = {


    //get kafka configs
    val topics = kafkaProps.kafkaTopic.split(",").toSeq
    val bootstrapServers = kafkaProps.bootstrapServers
    val offset = kafkaProps.offset
    val batchInterval = Seconds(kafkaProps.interval)
    val groupIds = kafkaProps.groupName


    println("==========================================================")
    println("||       Streaming Sensor Data From Kafka                ||")
    println("==========================================================")


    //Define streaming context with sparkContext and batchInterval
    val streamingContext = new StreamingContext(sc, batchInterval)


    //Parameters for kafka
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> bootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupIds,
      "auto.offset.reset" -> offset,
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )


    // crate dstreams
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    //    val sensorDataSchema = Encoders.product[SensorDataRaw].schema
    //    val sensorDataflatSchema = Encoders.product[SensorDataFlat].schema

    //perform some operation in each batch of streamed data
    stream.foreachRDD(rawRDD => {
      // Apply transformation for each rdd  obtained in batch Interval

      val rdd = rawRDD.map(_.value())

      //if dataframe is not empty convert to dataframe
      if (!rdd.isEmpty()) {

        val df = spark.read.json(rdd.toDS()).select("data.*")
          .select(
            "deviceId",
            "location.latitude",
            "location.longitude",
            "temperature",
            "time"
          )
          .withColumn("time", from_unixtime($"time" / 1000, "yyyy-MM-dd HH:mm:ssXXX"))

        df.printSchema

        df.show(false)
        writeToCassandra(df)
      }
    })


    streamingContext.start()

    try {
      streamingContext.awaitTermination()
      println("***************** streaming terminated  ***********************************")
    } catch {
      case e: Exception => {
        println("************* streaming exception caught in monitor thread  *****************")
      }
    }

    streamingContext.stop()


  }

  def writeToCassandra(df: DataFrame) = {

    val format = cassandraProps.format
    val table = cassandraProps.table
    val keyspace = cassandraProps.keyspace


    //Write data to cassandra table
    df.write.format(format)
      .options(
        Map("table" -> table, "keyspace" -> keyspace)
      )
      .mode(SaveMode.Append)
      .save()

  }

}
