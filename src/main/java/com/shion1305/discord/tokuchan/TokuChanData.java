package com.shion1305.discord.tokuchan;

import java.util.Map;

public class TokuChanData {
    private InstanceData[] instances;

    public TokuChanData() {
    }

    public TokuChanData(InstanceData[] instances) {
        this.instances = instances;
    }

    public TokuChanData(Object obj) {
        System.out.println("Object constructor called");
    }

    public InstanceData[] getInstances() {
        return instances;
    }

    public void setInstances(InstanceData[] instances) {
        this.instances = instances;
    }
}
