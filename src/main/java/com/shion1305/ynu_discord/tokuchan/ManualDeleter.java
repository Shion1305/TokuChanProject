package com.shion1305.ynu_discord.tokuchan;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;

public class ManualDeleter {
    private static String targetChannel = "899265124681007144";
    private static String messageID = "899265124681007144";
    public static void main(String[] args) {
        GatewayDiscordClient client=DiscordClient.create("ODk4OTAwOTcyNDI2OTE1ODUw.YWq8xA.N8n55agZO60WUmqUpiIRhlBIqHU").login().block();
        client.getMessageById(Snowflake.of(targetChannel),Snowflake.of("920628817381425162")).block().delete().block();
    }
}
