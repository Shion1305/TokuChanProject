package com.shion1305.ynu_discord.observation;

import discord4j.core.object.presence.Activity;

import java.util.ArrayList;
import java.util.List;

public class DiscordUserData {
    long userId = 0;
    String username;
    String avatarUrl;
    int presence = -1;
    String discriminator;
    List<Activity> activities;

    class Builder {
        private DiscordUserData data;

        public Builder() {
            data = new DiscordUserData();
        }

        public void setUserId(long id) {
            data.userId = userId;
        }

        public void setUsername(String username) {
            data.username = username;
        }

        public void setAvatarUrl(String url) {
            data.avatarUrl = url;
        }

        public void setPresence(int presence) {
            data.presence = presence;
        }

        public void setActivities(ArrayList<Activity> activities) {
            data.activities = activities;
        }

        public void setDiscriminator(String discriminator) {
            data.discriminator = discriminator;
        }

        public DiscordUserData toData() {
            if (data.userId != 0 && data.username != null && presence != -1) {
                return data;
            } else {
                throw new IllegalArgumentException("Missing Argument");
            }
        }
    }
}