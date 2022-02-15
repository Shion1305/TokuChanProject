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
        prefs = Preferences.userRoot().node("TokuChanProject");
    }

    static Preferences getInstancePreference(long groupId) {
        return prefs.node("Instances").node(String.valueOf(groupId));
    }

    static HashMap<Long, User> readUserdata(long groupId) {
        byte[] bData = getInstancePreference(groupId).getByteArray("UserData", null);
        if (bData != null) {
            try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bData))) {
                return (HashMap<Long, User>) stream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    static HashMap<Long, User> importOldData() {
        byte[] data = Preferences.userRoot().getByteArray("UserData", null);
        if (data == null) return null;
        try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(Preferences.userRoot().getByteArray("UserData", null)))) {
            return (HashMap<Long, User>) stream.readObject();
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
            preferences.putByteArray("UserData", bytes);
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
