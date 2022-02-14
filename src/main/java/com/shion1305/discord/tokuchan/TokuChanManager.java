package com.shion1305.discord.tokuchan;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TokuChanManager implements ServletContextListener {
    TokuChanInstance instance;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //    private static long targetChannel = 860701593149374466L;
        //    private static String targetChannel = "899265124681007144";
        long targetChannel = Long.parseLong(ConfigManager.getConfig("TargetChannel"));
        String token = ConfigManager.getConfig("DiscordToken");

        try {
            instance = new TokuChanInstance(token, targetChannel);
            instance.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        instance.stop();
    }
}
