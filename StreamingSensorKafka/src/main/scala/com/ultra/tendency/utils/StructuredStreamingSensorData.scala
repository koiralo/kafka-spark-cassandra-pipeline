package com.ultra.tendency.utils


import com.ultra.tendency.domain.{SensorDataFlat, SensorDataRaw}
import config.StreamingSettings
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.Seconds

object StructuredStreamingSensorData extends App {

  val sparkProps = StreamingSettings.sparStreaming
  val kafkaProps = StreamingSettings.KafkaConsumer
  val cassandraProps = StreamingSettings.cassandra


  val spark = SparkSession
    .builder()
    .appName(sparkProps.name)
    .master(sparkProps.master)
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

    val schema = Encoders.product[SensorDataRaw].schema

    val topics = kafkaProps.kafkaTopic
    val bootstrapServers = kafkaProps.bootstrapServers
    val offset = kafkaProps.offset
    val batchInterval = Seconds(kafkaProps.interval)
    val groupIds = kafkaProps.groupName


    println("==========================================================")
    println("||               Kafka Streaming Kafka                  ||")
    println("==========================================================")


    val df = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option("subscribe", topics)
      .option("startingOffsets", offset)
      .load()


    df.selectExpr("CAST(value AS STRING)").show(false)

    val rdd = df.selectExpr("CAST(value AS STRING)").rdd


    val dfNew = spark.read.json(rdd.map(_.getString(0)).toDS())

    schema.printTreeString()

    dfNew.show(false)
    dfNew.printSchema()

    /*    if (!finalDF.rdd.isEmpty()){
    //    println("==================================================")
    //    println("====>>>  " + finalDF.show(false))
        writeToCassandra(finalDF)
        }*/


  }

  def writeToCassandra(df: DataFrame) = {

    val format = cassandraProps.format
    val tableName = cassandraProps.table
    val keyspace = cassandraProps.keyspace

    /*    import org.apache.spark.sql.ForeachWriter
        val writer = new ForeachWriter [SensorDataFlat ] {
          override def open(partitionId: Long, version: Long) = true
          override def process(value: SensorDataFlat) = {
            processRow(value)
          }
          override def close(errorOrNull: Throwable) = {}
        }

        val query =
          ds.writeStream.queryName("kafka2Spark2Cassandra").foreach(writer).start

        query.awaitTermination()*/

    df.write
      .format(format)
      .options(Map("table" -> tableName, "keyspace" -> keyspace))
      .mode(SaveMode.Append)
      .save()
  }

}
