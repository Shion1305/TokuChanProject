package com.shion1305.ynu_discord.tokuchan;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TokuChanManager implements ServletContextListener {
    TokuChanHandler handler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //    private static long targetChannel = 860701593149374466L;
        //    private static String targetChannel = "899265124681007144";
        long targetChannel = Long.parseLong(ConfigManager.getConfig("TargetChannel"));
        String token = ConfigManager.getConfig("DiscordToken");

        try {
            handler = new TokuChanHandler(token, targetChannel);
            handler.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        handler.stop();
    }
}
