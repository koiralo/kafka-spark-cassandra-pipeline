# kafaka-spark-cassandra-pipeline 

Simple project which contains three modules for the pipeline, to send, stream, analysis IOT devices data. 
### Modules

* [SensorProducer](https://github.com/kornsanz/kafka-spark-cassandra-pipeline/tree/master/SensorProducer) - Kafka Producer to send data of IOT devices
* [StreamingSensorKafka](https://github.com/kornsanz/kafka-spark-cassandra-pipeline/tree/master/StreamingSensorKafka) - Spark Streaming from kafka and store to cassandra with some modifications
* [SparkBatch](https://github.com/kornsanz/kafka-spark-cassandra-pipeline/tree/master/SparkBatchJobs) - Batch Jobs to read from cassandra and perform analysis 



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


Got to the project root directory and create a jar file with maven '`mvn clean package'`
### Running Producer 

```java -jar SensorProducer/target/Sensor-Producer-1.0-jar-with-dependencies.jar ```

### Run Spark Streaming 

```./spark-submit StreamingSensorKafka/target/StreamingSensorKafka-1.0-jar-with-dependencies.jar ```

Performing analysis on data 

```scala
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


  def countPerDevice(ds: Dataset[SensorData], sqlContext: SQLContext): DataFrame ={
    import sqlContext.implicits._

    ds.groupBy($"deviceId")
      .agg(
        count($"temperature").as("count")
      )
  }
```

### Tools Used:
* Maven 3.5.0
* Java 1.8
* Scala 2.11.8
* Spark 2.3.0
* Kafka 2.11
* Cassandra 3.11
* Intellij
