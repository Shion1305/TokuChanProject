package com.shion1305.ynu_discord.tokuchan;

import java.io.Serializable;

public class User implements Serializable {
    int color;
    int tmp;

    User(int color, int tmp) {
        this.color = color;
        this.tmp = tmp;
    }
}
