package com.shion1305.discord.tokuchan.manual;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;

public class ManualDeleter {
    private static String targetChannel = "899265124681007144";
    private static String messageID = "899265124681007144";

    public static void main(String[] args) {
        deleteMessage("",targetChannel,messageID);
    }
    public static void deleteMessage(String discordToken,String targetChannel,String targetMessageID){
        GatewayDiscordClient client = DiscordClient.create(discordToken).login().block();
        client.getMessageById(Snowflake.of(targetChannel), Snowflake.of(targetMessageID)).block().delete().block();
    }
}
