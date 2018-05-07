/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author benfrenco
 */
public class DrugBankConfigurator {

    String pattern = "<drugbank-id[^\\>]*>([^\\<]*)<\\/drugbank-id>";
    String pattern_name = "[^\\<]*<name>([^\\<]*)";
    Pattern p = Pattern.compile(pattern);
    Pattern p_name = Pattern.compile(pattern_name);
    Matcher m;
    Matcher m_name;

    public ConcurrentHashMap<String, String> readDatabank(String path_db_id) throws FileNotFoundException {
        ConcurrentHashMap<String, String> mp = new ConcurrentHashMap<>();
        BufferedReader bfr = new BufferedReader(new FileReader(path_db_id));
        Iterator<String> iterator = bfr.lines().iterator();
        while (iterator.hasNext()) {
            String[] split = iterator.next().split("\\$");
            mp.put(split[0].toUpperCase(), split[1]);
        }
        return mp;
    }

    void createDatabank(String path_to_full_drugbank_xml, String output_db_id) throws FileNotFoundException, IOException {

        HashMap<String, String> mp = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(path_to_full_drugbank_xml));
        Iterator<String> iterator = br.lines().iterator();
        String actual_db_id = "";
        String actual_db_name = "";
        while (iterator.hasNext()) {
            String next = iterator.next();
            m = p.matcher(next);
            if (m.find()) {
                actual_db_id = m.group(1);
            } else {
                m_name = p_name.matcher(next);
                if (m_name.find()) {
                    actual_db_name = m_name.group(1);
                    if (mp.containsKey(actual_db_name)) {
                    } else {
                        mp.put(actual_db_name, actual_db_id);
                    }
                }
            }
        }

        FileWriter fww = new FileWriter(output_db_id);
        for (Map.Entry<String, String> entry : mp.entrySet()) {
            fww.write(entry.getKey() + "$" + entry.getValue() + "\n");
            fww.flush();
        }
        fww.close();
    }

}
