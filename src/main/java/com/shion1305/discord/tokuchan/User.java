package com.shion1305.discord.tokuchan;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -3431352608279128929L;
    int color;
    int tmp;

    User(int color, int tmp) {
        this.color = color;
        this.tmp = tmp;
    }
}
