package config

import com.typesafe.config.ConfigFactory

object Settings {
  private val config = ConfigFactory.load()

  object Producer {
    private val producer = config.getConfig("producer")

    lazy val interval = producer.getInt("interval")
    lazy val kafkaTopic = producer.getString("kafka.topic")

    lazy val bootstrapServers = producer.getString("kafka.bootstrap_servers")
    lazy val keySerializer = producer.getString("kafka.key_serializer")
    lazy val valueSerializer = producer.getString("kafka.value_serializer")

    lazy val groupName = producer.getString("kafka.group_name")

  }


}
