package com.engagepoint.university.admincentre.dao;

import java.io.IOException;
import java.util.List;

import com.engagepoint.university.admincentre.entity.Key;

public class Test2 {

    public static void main(String[] args) throws IOException {
/*
         Preferences preferences = new NodePreferences(null, "");
         Preferences another = preferences.node("/newNode/Node1/Node1.1");
         Preferences another1 = preferences.node("/newNode/Node1/Node1.2");
         Preferences another2 = preferences.node("/newNode/Node1");
        
         another.put("11", "11");
         another1.put("12", "12");
         another2.put("13", "13");

         try {
         another2.removeNode();
         } catch (BackingStoreException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }

        another1.put("14", "14");
        another2.put("15", "15");
        another1.put("16", "16");
        another2.put("17", "17");
*/
        KeyDAO keyDAO = KeyDAO.getInstance();
        List<Key> list = keyDAO.search("11");
    }
}
