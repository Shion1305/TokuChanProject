package com.shion1305.ynu_discord.tokuchan;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Presence;
import discord4j.discordjson.json.ActivityUpdateRequest;
import discord4j.gateway.intent.IntentSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

class TokuChanDiscordManager {
    private static HashMap<String, GatewayDiscordClient> clients;
    private static final Logger logger = Logger.getLogger("TokuChanDiscordManager");

    static GatewayDiscordClient getClient(String token) {
        logger.info("DiscordClient requested: " + token.substring(token.length() - 10));
        if (clients == null) {
            clients = new HashMap<>();
        }
        if (clients.containsKey(token)) return clients.get(token);
        GatewayDiscordClient client = DiscordClient.create(token).gateway().setEnabledIntents(IntentSet.all()).setInitialPresence(s -> Presence.online(ActivityUpdateRequest.builder().type(0).name("!introで使い方を確認! メッセージはDMで送信してね!").url("https://cdn.discordapp.com/avatars/898900972426915850/4b09f00b8b78094e931641a85077bcc3.png?size=512").build())).login().block();
        clients.put(token, client);
        return client;
    }
}
