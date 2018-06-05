# kafaka-spark-cassandra-pipeline 

Simple project which contains three modules for the pipeline, to send, stream, analysis IOT devices data. 
### Modules

* [SensorProducer]() - Kafka Producer to send data of IOT devices
* [StreamingSensorKafka]() - Spark Streaming from kafka and store to cassandra with some modifications
* [SparkBatch]() - Batch Jobs to read from cassandra and perform analysis 



### Prerequisites

To run the project Following things should be required and running
* Maven 
* Kafka 0.10 + 
* Cassandra 3.0 + 
* Spark 2.3.0 

## Running and using the project

Make sure the Kafka, cassandra and spark is running. 

Create a kafka topic to send the data as 

```
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic sensor-events
```

Create Cassandra Table to store the data 
```
CREATE TABLE sensor (
                   "deviceId" text,
                   "temperature" long,
                   "latitude" double,
                   "longitude" double,
                   "time" text,
                   PRIMARY KEY("deviceId", "time")
                   );
```
Make the changes in the config in each modules which is in ```ModuleName/src/main/resources/*.config``` file.
For example :
```
producer {
  interval = 1  //sends data every 1 second 
  kafka = {
    topic = "sensor-events"             // kafak topic 
    bootstrap_servers = "localhost:9092"    // kaka bootstrap servers
    key_serializer = "org.apache.kafka.common.serialization.StringSerializer"
    value_serializer = "org.apache.kafka.common.serialization.StringSerializer"
    group_name = "IOTSensorEvents"   //kafka group name
  }
}
``` 


### Installing



```
Give the example
```

