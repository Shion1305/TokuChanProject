package com.shion1305.discord.tokuchan.instance;

import com.shion1305.discord.tokuchan.UserConverter;
import com.shion1305.discord.tokuchan.instance.model.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

class TokuChanPreferencesManager {
    private static final Preferences prefs;
    private static final Logger logger = Logger.getLogger(TokuChanPreferencesManager.class.getName());

    /* Preferenceを占有的に管理するクラス
     */
    static {
        prefs = Preferences.userRoot().node("tokuchanproject");
    }

    static Preferences getInstancePreference(long groupId) {
        return prefs.node("instances").node(String.valueOf(groupId));
    }

    static HashMap<Long, User> readUserdata(long groupId) {
        byte[] bData = getInstancePreference(groupId).getByteArray("userdata", null);
        if (bData != null) {
            try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bData))) {
                try {
                    return (HashMap<Long, User>) stream.readObject();
                } catch (ClassNotFoundException e) {
                    HashMap<Long, com.shion1305.discord.tokuchan.User> tmp = (HashMap<Long, com.shion1305.discord.tokuchan.User>) stream.readObject();
                    HashMap<Long, User> newData = new HashMap<>();
                    for (Map.Entry<Long, com.shion1305.discord.tokuchan.User> entry : tmp.entrySet()) {
                        newData.put(entry.getKey(), UserConverter.convert(entry.getValue()));
                    }
                    return newData;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("NO USER_Data_Found");
        }
        return new HashMap<>();
    }

    static void saveData(long groupId, HashMap<Long, User> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(data);
            byte[] bytes = baos.toByteArray();
            Preferences preferences = getInstancePreference(groupId);
            preferences.putByteArray("userdata", bytes);
            preferences.flush();
        } catch (Exception e) {
            logger.warning("SAVE FAILED");
            logger.warning(e.toString());
            logger.warning(e.getLocalizedMessage());
            logger.warning(e.getCause().getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
