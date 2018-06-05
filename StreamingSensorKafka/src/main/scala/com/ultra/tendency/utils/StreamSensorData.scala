package com.ultra.tendency.utils

import java.util.Properties

import com.ultra.tendency.domain.{ SensorDataRaw}
import config.StreamingSettings
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkContext, TaskContext}
import org.apache.spark.sql.{Encoders, SQLContext, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object StreamSensorData extends App {

  val kafkaProps = StreamingSettings.KafkaConsumer
  val cassandraProps = StreamingSettings.KafkaConsumer


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

  startStreamingKafka()


  def startStreamingKafka(): Unit = {


    val topics = kafkaProps.kafkaTopic.split(",").toSeq
    val bootstrapServers = kafkaProps.bootstrapServers
    val offset = kafkaProps.offset
    val batchInterval = Seconds(kafkaProps.interval)
    val groupIds = kafkaProps.groupName


    println("==========================================================")
    println("||               Kafka Streaming Kafka                  ||")
    println("==========================================================")


    //Define streaming context with sparkContext and batchInterval
    val streamingContext = new StreamingContext(sc, batchInterval)


    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> bootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupIds,
      "auto.offset.reset" -> offset,
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )


    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val sensorDataSchema = Encoders.product[SensorDataRaw].schema
//    val sensorDataflatSchema = Encoders.product[SensorDataFlat].schema

    stream.foreachRDD(rawRDD => {
      // Apply transformation for each rdd  obtained in batch Interval

      val rdd = rawRDD.map(_.value())

      if (!rdd.isEmpty()) {

        val df = spark.read.json(rdd.toDS()).select("data.*")
          .select(
            "deviceId",
            "location.latitude",
            "location.longitude",
            "temperature",
            "time"
          )
            .withColumn("time", from_unixtime($"time" /1000, "yyyy-MM-dd'T'HH:mm:ss:SSS"))
//          .as[SensorDataFlat]

        df.printSchema()
        df.show(false)
      }
    })

//    “yyyy-MM-dd'T'HH:mm:ssXXX”.

    streamingContext.start()

    try {
      streamingContext.awaitTermination()
      println("*** streaming terminated")
    } catch {
      case e: Exception => {
        println("*** streaming exception caught in monitor thread")
      }
    }

    streamingContext.stop()


  }
}
