package com.engagepoint.university.admincentre;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.preferences.NodePreferences;


public class ConsoleController {


    private final static StringBuilder ALIGN_STRING = new StringBuilder("---");
    private Preferences currentPreferences = new NodePreferences(null, "");

    public Preferences getCurrentPreferences() {
        return currentPreferences;
    }

    public void setCurrentPreferences(Preferences currentPreferences) {
        this.currentPreferences = currentPreferences;
    }

    public void showHelp() {
        System.out.println("Options ...");
        for (Commands commands : Commands.values()) {
            String name = commands.getName();
            StringBuilder stringBuilder = buildAlignmentString(name.length());
            System.out.println("  " + name + stringBuilder + commands.getDescription());
        }
        System.out.println();
    }

    private StringBuilder buildAlignmentString(int length) {
        int fixLength = 30;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fixLength - length; i++) {
            stringBuilder = stringBuilder.append(" ");
        }
        return stringBuilder;
    }

    public void displayNodes(Preferences preference) {
        System.out.println(ALIGN_STRING + " name = " + preference.name());
        displayKeys(preference);
        try {
            if (preference.childrenNames().length != 0) {
                ALIGN_STRING.insert(0, "   ");
                System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
                System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");

                for (int i = 0; i < preference.childrenNames().length; i++) {
                    displayNodes(preference.node(preference.childrenNames()[i]));
                }
                ALIGN_STRING.delete(0, 3);

            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void showVersion() {
        System.out.println("Current application version is " + 1.0);
    }

    private void displayKeys(Preferences preferance) {
        String[] keys;
        try {
            keys = preferance.keys();
            if (keys.length != 0) {
                for (int i = 0; i < keys.length; i++) {

                    System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3)
                            + " Key = " + keys[i] + ";" + "Value = "
                            + preferance.get(keys[i], "value wasn`t found"));

                }
                System.out.println();
            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean selectNode(String nodeId) {
        this.currentPreferences = currentPreferences.node(nodeId);
        displayNodes(currentPreferences);
        return true;

    }

    public void createNode(String nodeName) {
        if (nameValidation(nodeName)) {
            String newPath = (currentPreferences.absolutePath().equals("/") ? "/" + nodeName
                    : currentPreferences.absolutePath() + "/" + nodeName);
            currentPreferences.node(newPath);
            currentPreferences.node(currentPreferences.absolutePath());
        }
        displayNodes(this.currentPreferences);
    }

    public void createKey(String keyName, String keyType, String keyValue) {
        if (nameValidation(keyName) && keyTypeValidation(keyType)) {
            currentPreferences.put(keyName, keyValue);
        }
        displayNodes(currentPreferences);

    }

    public boolean nameValidation(String name) {
        boolean value = name.matches("^[A-Z][a-z0-9]+([A-Z][a-z0-9]+|[A-Z]$)*$");
        if (!value) {
            System.out.println("You enter not valid name...");
        }
        return value;
    }

    /**
     * Allows to verify entered key type from console
     * 
     * @param keyType
     *            String param which comes from console
     * @return true if key type exist in enum KeyType
     */
    public boolean keyTypeValidation(String keyType) {
        try {
            KeyType.valueOf(keyType);
        } catch (IllegalArgumentException e) {
            KeyType[] keyTypeList = KeyType.values();
            System.out.println("You enter wrong key type. Use one of the next types :");
            for (KeyType keyTypeTemp : keyTypeList) {
                System.out.println("  " + keyTypeTemp.toString());
            }
            return false;
        }
        return true;

    }

}