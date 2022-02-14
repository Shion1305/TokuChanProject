package com.shion1305.discord.tokuchan;

import java.io.Serializable;

public class User implements Serializable {
    int color;
    int tmp;

    User(int color, int tmp) {
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
