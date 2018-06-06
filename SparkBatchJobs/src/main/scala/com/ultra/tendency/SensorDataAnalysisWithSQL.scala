package com.ultra.tendency

import com.ultra.tendency.domain.SensorData
import config.BatchJobsSetting
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

object SensorDataAnalysisWithSQL extends App {

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


  val ddl =
    """CREATE TEMPORARY VIEW sensor
     USING org.apache.spark.sql.cassandra
     OPTIONS (
     table "sensor",
     keyspace "iotdata",
     pushdown "true")"""


  spark.sql(ddl)

  spark.sql("SELECT sensor.deviceId, max(sensor.temperature) AS maxTemp FROM sensor group by sensor.deviceId").show(false)

  spark.sql("SELECT sensor.deviceId, count(sensor.temperature) AS count FROM sensor group by sensor.deviceId").show(false)

  spark.sql("SELECT * FROM sensor where CAST(sensor.time AS DATE) = '2018-06-05' ").show(false)


  spark.close()

}
