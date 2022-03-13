package com.shion1305.discord.tokuchan;

public class UserConverter {
    public static com.shion1305.discord.tokuchan.instance.model.User convert(User user) {
        return new com.shion1305.discord.tokuchan.instance.model.User(user.color, user.tmp);
    }
}
