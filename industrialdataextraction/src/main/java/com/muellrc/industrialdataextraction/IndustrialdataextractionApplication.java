package com.muellrc.industrialdataextraction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3RxClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3PublishResult;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import com.muellrc.industriandataextraction.model.Configuration;
import com.muellrc.industriandataextraction.model.PlcFieldConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class IndustrialdataextractionApplication {

    private static final Logger logger = LoggerFactory.getLogger(IndustrialdataextractionApplication.class);

    private Configuration config;

    
    public IndustrialdataextractionApplication() {}
    
    private IndustrialdataextractionApplication(String propsPath) {
        if(StringUtils.isEmpty(propsPath)) {
            logger.error("Empty configuration file parameter");
            throw new IllegalArgumentException("Empty configuration file parameter");
        }
        File propsFile = new File(propsPath);
        if(!(propsFile.exists() && propsFile.isFile())) {
            logger.error("Invalid configuration file {}", propsFile.getPath());
            throw new IllegalArgumentException("Invalid configuration file " + propsFile.getPath());
        }
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            config = mapper.readValue(propsFile, Configuration.class);
        } catch (IOException e) {
            logger.error("Error parsing configuration", e);
        }
    }

    private void run() throws PlcException {
        // Create a new MQTT client.
        final Mqtt3RxClient client = MqttClient.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(config.getMqttConfig().getServerHost())
            .serverPort(config.getMqttConfig().getServerPort())
            .useMqttVersion3()
            .buildRx();

        // Connect to the MQTT broker.
        final Single<Mqtt3ConnAck> connAckSingle = client.connect().timeout(10, TimeUnit.SECONDS);
        System.out.println("Connected to MQTT Broker!" + client.toString());

        // Connect to the PLC.
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection(config.getPlcConfig().getConnection())) {

            // Check if this connection support reading of data.
            if (!plcConnection.getMetadata().canRead()) {
                System.err.println("This connection doesn't support reading.");
                return;
            }

            // Create a new read request.
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
            for (PlcFieldConfig fieldConfig : config.getPlcConfig().getPlcFields()) {
                builder = builder.addItem(fieldConfig.getName(), fieldConfig.getAddress());
            }
            PlcReadRequest readRequest = builder.build();

            // Send a message containing the PLC read response.
            Flowable<Mqtt3Publish> messagesToPublish = Flowable.generate(emitter -> {
                PlcReadResponse response = readRequest.execute().get();
                String jsonPayload = getPayload(response);
                final Mqtt3Publish publishMessage = Mqtt3Publish.builder()
                    .topic(config.getMqttConfig().getTopicName())
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .payload(jsonPayload.getBytes())
                    .build();
                emitter.onNext(publishMessage);
            });

            // Emit 1 message only every 100 milliseconds.
            messagesToPublish = messagesToPublish.zipWith(Flowable.interval(
                config.getPollingInterval(), TimeUnit.MILLISECONDS), (publish, aLong) -> publish);

            final Single<Mqtt3ConnAck> connectScenario = connAckSingle
                .doOnSuccess(connAck -> System.out.println("Connected with return code " + connAck.getReturnCode()))
                .doOnError(throwable -> System.out.println("Connection failed, " + throwable.getMessage()));

            final Flowable<Mqtt3PublishResult> publishScenario = client.publish(messagesToPublish)
                .doOnNext(publishResult -> System.out.println(
                    "Publish acknowledged: " + new String(publishResult.getPublish().getPayloadAsBytes())));

            connectScenario.toCompletable().andThen(publishScenario).blockingSubscribe();
        } 
        catch (Exception e) {
            throw new PlcException("Error creating connection to " + config.getPlcConfig().getConnection(), e);
        }
    }

    private String getPayload(PlcReadResponse response) {
        JsonObject jsonObject = new JsonObject();
        response.getFieldNames().forEach(fieldName -> {
            if(response.getNumberOfValues(fieldName) == 1) {
                jsonObject.addProperty(fieldName, response.getObject(fieldName).toString());
            } else if (response.getNumberOfValues(fieldName) > 1) {
                JsonArray values = new JsonArray();
                response.getAllBytes(fieldName).forEach(values::add);
                jsonObject.add(fieldName, values);
            }
        });
        System.out.println(jsonObject.toString());
        return jsonObject.toString();
    }
    
	public static void main(String[] args) {
		SpringApplication.run(IndustrialdataextractionApplication.class, args);

                IndustrialdataextractionApplication industrialdataextractor = new IndustrialdataextractionApplication("extractor-config.yml");
               
                try {
                    industrialdataextractor.run();
                    } catch (PlcException e) {
                   }
                
                
	}

}
