package com.ultra.tendency

import java.util.Properties

import com.ultra.tendency.utils.MainProducer.producerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object TestProducer extends App {


  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)



  val producerRecord = new ProducerRecord("sensor", "" , "{\"hi hello \"}")
  val message = producer.send(producerRecord)


  println(message.get().offset())
  println("data send to producer")


}
