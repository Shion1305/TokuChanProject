package com.shion1305.discord.tokuchan.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shion1305.discord.tokuchan.manager.model.InstanceData;
import com.shion1305.discord.tokuchan.manager.model.TokuChanData;
import com.shion1305.discord.tokuchan.instance.TokuChanInstance;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebListener
public class TokuChanManager implements ServletContextListener {
    private static final List<TokuChanInstance> instances = new ArrayList<>();
    private final static Logger logger = Logger.getLogger(TokuChanManager.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            logger.info(ConfigManager.getConfig("TokuChanConfigJson"));
            TokuChanData data = mapper.readValue(new File(ConfigManager.getConfig("TokuChanConfigJson")), TokuChanData.class);
            if (data == null) {
                logger.severe("JsonConfig\"" + ConfigManager.getConfig("TokuChanConfigJson") + "\" is not loaded properly");
                return;
            }
            for (InstanceData instanceData : data.getInstances()) {
                if (!instanceData.isValid()) {
                    logger.info("Skipped loading InstanceData\"" + instanceData.getName() + "\" as it is invalid...");
                    continue;
                }
                TokuChanInstance instance = new TokuChanInstance(instanceData);
                instances.add(instance);
            }
        } catch (IOException e) {
            logger.severe("ERROR OCCURRED DURING LOADING JSON_CONFIG");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (TokuChanInstance i : instances) {
            i.stop();
        }
    }
}


