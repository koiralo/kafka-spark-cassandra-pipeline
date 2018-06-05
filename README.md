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

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
