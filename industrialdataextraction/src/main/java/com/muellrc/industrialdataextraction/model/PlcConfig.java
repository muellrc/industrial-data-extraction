package com.muellrc.industriandataextraction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PlcConfig {

    private String connection;
    @JsonProperty("memory-blocks")
    private List<PlcMemoryBlock> plcMemoryBlocks;
    @JsonProperty("addresses")
    private List<PlcFieldConfig> plcFields;

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public List<PlcMemoryBlock> getPlcMemoryBlocks() {
        return plcMemoryBlocks;
    }

    public void setPlcMemoryBlocks(List<PlcMemoryBlock> plcMemoryBlocks) {
        this.plcMemoryBlocks = plcMemoryBlocks;
    }

    public List<PlcFieldConfig> getPlcFields() {
        return plcFields;
    }

    public void setPlcFields(List<PlcFieldConfig> plcFields) {
        this.plcFields = plcFields;
    }

}
