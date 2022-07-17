package com.muellrc.industriandataextraction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttConfig {

    @JsonProperty("topic-name")
    private String topicName;
    @JsonProperty("server-host")
    private String serverHost;
    @JsonProperty("server-port")
    private int serverPort;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

}
