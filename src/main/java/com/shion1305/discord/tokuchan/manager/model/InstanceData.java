package com.shion1305.discord.tokuchan.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceData {
    private final String name, discordToken, description, statusMessage;
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
                        @JsonProperty("targetChannelId") long targetChannelId,
                        @JsonProperty("description") String description,
                        @JsonProperty("status") String statusMessage) {
        this.name = Objects.requireNonNullElse(name, "Instance-FOR-" + targetChannelId);
        this.discordToken = discordToken;
        this.targetGuildId = targetGuildId;
        this.targetChannelId = targetChannelId;
        this.description = Objects.requireNonNullElse(description, "やぁ!  匿名化BOTの匿ちゃんだよ!\n" +
                "私にDMしてくれたら自動的に匿名チャンネルに転送するよ!\n" +
                "送信取り消しも可能!\n" +
                "質問しづらい事、答えにくい事、発言しつらい事などあったら気軽に使ってみてね!\n" +
                "\n" +
                "プロフィール(色/番号)は、いつでもリセットすることが可能です!");
        this.statusMessage = Objects.requireNonNullElse(statusMessage, "!introで使い方を確認! メッセージはDMで送信してね!");
    }


    public long getTargetChannelId() {
        return targetChannelId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValid() {
        return discordToken != null && targetChannelId != 0L && targetGuildId != 0L;
    }

    public String getName() {
        return name;
    }
}