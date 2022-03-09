package com.shion1305.discord.tokuchan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokuChanData {
    private final InstanceData[] instances;

    public TokuChanData(@JsonProperty("instances") InstanceData[] instances) {
        this.instances = instances;
    }

    public InstanceData[] getInstances() {
        return instances;
    }
}
