---
sidebar_position: 11
---

# Kafka

Apache Kafka is a distributed messaging queue which provides low latency and reliable data delivery.

## Creating an app connection

Configure the kafka cluster servers using the bootstrap-server config. Define the servers in the format *host:post, anotherhost:port*.

## Creating a sync pipeline

### Configure the sync settings

Select the topic to which you want to sync the data from a drop down.

### Kafka Fields Mapping

Mapping page for the kafka connector will be prepopulated with the incoming source query fields. You are free to change the mapping as a free text to determine the payload which will be published to kafka
