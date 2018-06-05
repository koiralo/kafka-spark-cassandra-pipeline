package config

import com.typesafe.config.ConfigFactory

object BatchJobsSetting {

  private val config = ConfigFactory.load()

  object spark {
    private val spark = config.getConfig("streamer.spark")

    lazy val master = spark.getString("master")
    lazy val name = spark.getString("app_name")

  }

  object cassandra {

    private val cassandra = config.getConfig("streamer.cassandra")

    lazy val format  = cassandra.getString("format")
    lazy val host = cassandra.getString("host")

    lazy val user = cassandra.getString("user")
    lazy val pass = cassandra.getString("pass")
    lazy val keyspace  = cassandra.getString("keyspace")
    lazy val table   = cassandra.getString("table")

  }

}
