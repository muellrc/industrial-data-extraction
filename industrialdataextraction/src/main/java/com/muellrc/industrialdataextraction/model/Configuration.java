package com.muellrc.industriandataextraction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {

    @JsonProperty("mqtt")
    private MqttConfig mqttConfig;
    @JsonProperty("plc")
    private PlcConfig plcConfig;
    @JsonProperty("polling-interval")
    private int pollingInterval;

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    public PlcConfig getPlcConfig() {
        return plcConfig;
    }

    public void setPlcConfig(PlcConfig plcConfig) {
        this.plcConfig = plcConfig;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

}
