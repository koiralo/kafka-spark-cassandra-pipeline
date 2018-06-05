package com.ultra.tendency

import config.BatchJobsSetting
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import com.ultra.tendency.AnalysisFunctions._
import com.ultra.tendency.domain.SensorData

object SensorDataAnalysis extends App {

  val cassandraProps = BatchJobsSetting.cassandra
  val sparkProps = BatchJobsSetting.spark


  //create an entry point as spark sesion

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

  spark.conf.set("spark.cassandra.connection.host", cassandraProps.host)
  //  spark.conf.set("spark.cassandra.auth.username", cassandraProps.user)
  //  spark.conf.set("spark.cassandra.auth.password", cassandraProps.pass)


  val format = cassandraProps.format
  val table = cassandraProps.table
  val keyspace = cassandraProps.keyspace


  //load data from cassanda
  val data = spark.read.format("org.apache.spark.sql.cassandra")
    .options(Map("table" -> table, "keyspace" -> keyspace))
    .load()
    .as[SensorData]
    .persist()


  val maxTempDF = maxTempPerDevice(data, sqlContext)

  maxTempDF.show(false)

  val givenDay = "2018-06-05"

  val maxTempDayDF = maxTempOnGivenDay(data, sqlContext, givenDay)

  maxTempDayDF.show(false)



}
