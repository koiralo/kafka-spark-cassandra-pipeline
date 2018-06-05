package com.ultra.tendency

import com.ultra.tendency.domain.SensorData
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, Dataset, SQLContext}
import org.apache.spark.sql.functions.{col, max, min}
import org.apache.spark.sql.types.DateType

object AnalysisFunctions extends Serializable {

  def maxTempPerDevice(ds: Dataset[SensorData], sqlContext: SQLContext): DataFrame = {

    import sqlContext.implicits._

    ds.groupBy($"deviceId")
      .agg(
        max($"temperature").as("maxTemperature"),
        min($"temperature").as("minTemperature"))
  }

  def maxTempOnGivenDay(ds: Dataset[SensorData], sqlContext: SQLContext, date: String): DataFrame = {

    import sqlContext.implicits._

    ds.filter($"time".cast(DateType) === date)
      .groupBy($"deviceId", $"time".cast(DateType).as("day"))
      .agg(
        max($"temperature").as("maxTemperature"),
        min($"temperature").as("maxTemperature")
      )
  }

}
