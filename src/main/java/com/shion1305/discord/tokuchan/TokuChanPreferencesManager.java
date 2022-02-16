package com.shion1305.discord.tokuchan;

import java.io.*;
import java.util.HashMap;
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
                return (HashMap<Long, User>) stream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("NO USER_Data_Found");
        }
        return new HashMap<>();
    }

    static HashMap<Long, com.shion1305.ynu_discord.tokuchan.User> importOldData() {
        byte[] data = Preferences.userRoot().getByteArray("UserData", null);
        if (data!=null)logger.info("OLD DATA DETECTED");
        if (data == null) return null;
        try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (HashMap<Long, com.shion1305.ynu_discord.tokuchan.User>) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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
