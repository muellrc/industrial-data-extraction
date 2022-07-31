# industrial-data-extraction

## Introduction

<img alt="image" src="https://user-images.githubusercontent.com/89973885/166163284-a914ac1c-56a7-462b-a25a-d61c3d54e6dc.png">

A Docker network is used to ensure security.<br />

## How to Run

To run this project, simply call docker-compose:<br />

**Step 1:** docker-compose build <br />
**Step 2:** docker-compose up <br />
**Step 3:** Monitor the status updates in the data-extractor container, until data is being published to the MQTT broker:  <br />

```
MQTT Connection Starting...
Connected to MQTT Broker!
INFO 1 --- [           main] org.apache.plc4x.java.PlcDriverManager   : Instantiating new PLC Driver Manager with class loader org.springframework.boot.loader.LaunchedURLClassLoader@5e9f23b4
INFO 1 --- [           main] org.apache.plc4x.java.PlcDriverManager   : Registering available drivers...
INFO 1 --- [           main] org.apache.plc4x.java.PlcDriverManager   : Registering driver for Protocol modbus (Modbus)
INFO 1 --- [           main] o.a.p.j.transport.tcp.TcpChannelFactory  : Configuring Bootstrap with Configuration{}
Connected with return code SUCCESS
{"400002":"84","400003":"3"}
Publish acknowledged: {"400002":"84","400003":"3"}
```
<br />

**Step 4:** Open the Grafana dashboard in http://127.0.0.1:8080/d/QPhOqGz4z/modbus-registers?orgId=1


## Components:

The following containers are used to provide the functionality described above:

- modbus-client: Container based on a standard Python Docker image containing a script implementing the Modbus protocol, acting as a Modbus Client device and writing data to the Modbus Server

- modbus-server: Container based on a standard Python Docker image containing a script implementing the Modbus protocol, acting as a Modbus Server in the network

- data-exctractor: Container based on a standard Maven Docker image with the Java code connecting to the Modbus devices via Apache PLC4X library, and pushing data to the MQTT Broker.

- hivemq: Container based on a standard HiveMQ Community Edition Docker image acting as the MQTT Broker in the network.

- telegraf: Container based on a standard Telegraf Docker image acting as the server integration agent in the network, connecting to the MQTT Broker and publishing timeseries data into InfluxDB.

- influxdb: Container based on a standard InfluxDB Docker image, storing timeseries data originally queried from the Modbus devices.

- dev-grafana: Container based on a standard Grafana Docker image, with a dashboard called Modbus Registers provisioned via Dockerfile, connecting to the InfluxDB timeseries database.
