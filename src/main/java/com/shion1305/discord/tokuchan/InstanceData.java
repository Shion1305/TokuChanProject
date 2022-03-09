package com.shion1305.discord.tokuchan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceData {
    private final String discordToken;
    private final long targetGuildId, targetChannelId;

    public String getDiscordToken() {
        return discordToken;
    }

    public long getTargetGuildId() {
        return targetGuildId;
    }

    public InstanceData(@JsonProperty("discordToken") String discordToken,
                        @JsonProperty("targetGuildId") long targetGuildId,
                        @JsonProperty("targetChannelId") long targetChannelId) {
        this.discordToken = discordToken;
        this.targetGuildId = targetGuildId;
        this.targetChannelId = targetChannelId;
    }

    public long getTargetChannelId() {
        return targetChannelId;
    }
}
