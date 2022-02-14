package com.shion1305.discord.tokuchan;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.prefs.Preferences;

class TokuChanPreferencesManager {
    private static final Preferences prefs;

    /* Preferenceを占有的に管理するクラス
     */
    static {
        prefs = Preferences.userNodeForPackage(TokuChanPreferencesManager.class);
    }

    static Preferences getInstancePreference(String groupId) {
        return prefs.node("Instances").node(groupId);
    }

    static HashMap<Long, User> readUserdata(long groupId) {
        try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(getInstancePreference(String.valueOf(groupId)).getByteArray("UserData", null)))) {
            return (HashMap<Long, User>) stream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void importOldData() {

    }


}
