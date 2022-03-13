package com.shion1305.discord.tokuchan.manager;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Discordのキーを安全かつ効率的に管理するためのクラス
 * TokuChanHanlerインスタンスを呼び出す。
 */
public class TokuChanDiscordManager {
    private static HashMap<String, GatewayDiscordClient> clients;
    private static final Logger logger = Logger.getLogger("TokuChanDiscordManager");
    public static GatewayDiscordClient getClient(String token,String statusMessage) {
        logger.info("DiscordClient requested: " + token.substring(token.length() - 10));
        if (clients == null) {
            clients = new HashMap<>();
        }
        if (clients.containsKey(token)) return clients.get(token);
        GatewayDiscordClient client = DiscordClient.create(token).gateway().setEnabledIntents(IntentSet.all()).setInitialPresence(s -> ClientPresence.online(ClientActivity.of(Activity.Type.COMPETING, statusMessage, "https://cdn.discordapp.com/avatars/898900972426915850/4b09f00b8b78094e931641a85077bcc3.png?size=512"))).login().block();
        clients.put(token, client);
        return client;
    }
}
