package com.shion1305.discord.tokuchan.instance.model;

import java.io.Serializable;

public class User implements Serializable {
    public final int color;
    public final int tmp;

    public User(int color, int tmp) {
        this.color = color;
        this.tmp = tmp;
    }

    @Override
    public String toString() {
        return "User{" +
                "color=" + color +
                ", tmp=" + tmp +
                '}';
    }
}
