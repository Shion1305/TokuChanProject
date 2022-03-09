package com.shion1305.discord.tokuchan.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceData {
    public final String name;
    private final String discordToken;
    private final long targetGuildId, targetChannelId;

    public String getDiscordToken() {
        return discordToken;
    }

    public long getTargetGuildId() {
        return targetGuildId;
    }

    public InstanceData(@JsonProperty("name") String name,
                        @JsonProperty("discordToken") String discordToken,
                        @JsonProperty("targetGuildId") long targetGuildId,
                        @JsonProperty("targetChannelId") long targetChannelId) {
        if (name == null) this.name = "Instance-FOR-" + targetChannelId;
        else this.name = name;
        this.discordToken = discordToken;
        this.targetGuildId = targetGuildId;
        this.targetChannelId = targetChannelId;
    }

    public long getTargetChannelId() {
        return targetChannelId;
    }

    public boolean isValid() {
        return discordToken != null && targetChannelId != 0L && targetGuildId != 0L;
    }
}
