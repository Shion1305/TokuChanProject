package com.shion1305.discord.tokuchan;

public class InstanceData {
    String discordToken;
    long targetGuildId, targetChannelId;

    public String getDiscordToken() {
        return discordToken;
    }

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
    }

    public long getTargetGuildId() {
        return targetGuildId;
    }

    public InstanceData() {
    }

    public void setTargetGuildId(long targetGuildId) {
        this.targetGuildId = targetGuildId;
    }

    public long getTargetChannelId() {
        return targetChannelId;
    }

    public void setTargetChannelId(long targetChannelId) {
        this.targetChannelId = targetChannelId;
    }

    public InstanceData(String discordToken) {
        this.discordToken = discordToken;
    }
}
