package com.shion1305.discord.tokuchan.manager;

import com.shion1305.discord.tokuchan.manager.model.InstanceData;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TokuChanManagerTest {
    private static final Logger LOGGER = Logger.getLogger(TokuChanManagerTest.class.getName());

    @Test
    public void ConfigLoadTest() {
        var d = TokuChanManager.readTokuChanData();
        assertNotNull(d);
        for (InstanceData da : d.getInstances()) {
            LOGGER.info("Testing... " + da.getName());
            assertTrue(da.isValid());
        }
    }
}
