/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import static de.master.neo4jdbbuilder.Properties.basic_path_database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author maestro
 */
public class PreProcessorYCS_F extends YcsSuperInstanz_F {

    String symb = Tools.OSValidator();
    String directory = Tools.CurrenDirectory();
    String rel_path = directory + symb + basic_path_database + symb + Properties.ycs_dir;
    String path_c_all = directory + symb + "config" + symb + "c_all.txt";
    String rel_path_props = "";
    String drug_name_pattern = "((\\x20*)\"{1})(display_name)(\"{1}:{1}\\x20*\"{1})(.*)(\"{1})";
    String drug_link_pattern = "((\\x20*)\"{1})(file_name)(\"{1}:{1}\\x20*\"{1}\\.{1})(.*)(\"{1})";
    String[] drug_case_properties;
    String[] drug_drug_properties;
    String[] drug_event_properties;
    String[] drug_info_properties;
    String[] drug_summary_properties;
    HashMap<String, String[]> parsePropsCase = new HashMap();
    HashMap<String, String[]> parsePropsDrug = new HashMap();
    HashMap<String, String[]> parsePropsEvent = new HashMap();
    HashMap<String, String[]> parsePropsInfo = new HashMap();
    HashMap<String, String[]> parsePropsSummary = new HashMap();

    HashMap<String, Integer> get_reports;
    HashMap<String, Integer> get_patients;
    HashMap<String, Integer> get_drug;
    HashMap<String, Integer> get_reactions;
    HashMap<String, Integer> get_outcome;
    static HashMap<String, Long> patient_for_link = new HashMap<>();

    String regex_summary_N = "(N{1})(,{1})(.*)";
    String regex_summary_Y = "(Y{1})(,{1})(.*)";
    Pattern pattern_summary_N = Pattern.compile(regex_summary_N);
    Pattern pattern_summary_Y = Pattern.compile(regex_summary_Y);
    String präfix_ycs = Properties.präfix_ycs;
    String meddra;

    GraphDatabaseService db;

    String pattern_property = "(\\d){1}(:\\x20){1}(.*)";
    int pattern_finisher;
    static int counter_process = 0;
    static int counter_max;

    String jason_file;
    File db_file;
    String download_folder;
    String output_folder;

    public void clearAllMaps() {
        patient_for_link.clear();
        db.shutdown();
    }

    public PreProcessorYCS_F(File f, String jason_file, String download_folder, String output_folder) {
        this.jason_file = jason_file;
        this.db_file = f;
        this.download_folder = download_folder;
        this.output_folder = output_folder;
    }

    HashMap<String, HashSet<String>> startPreProcessingAll() throws IOException {
        System.out.println("starting");
        String link_file = doDownloadAndExtract(jason_file, download_folder);
        HashMap<String, String> allLinks = getAllLinks(download_folder, link_file);
        HashMap<String, HashSet<String>> doDownloadOfAllandEXTRACTION = doDownloadOfAllandEXTRACTION(allLinks, download_folder);
        counter_max = doDownloadOfAllandEXTRACTION.size();

        doParsingOfProperties();
        HashMap<String, HashMap<String, Integer>> map = creatingAllPropsMapping();
        get_reports = map.get("Report");
        get_patients = map.get("Patient");
        get_drug = map.get("Drug");
        get_reactions = map.get("Reaction");
        get_outcome = map.get("Outcome");
        meddra = getMeddraVersion(doDownloadOfAllandEXTRACTION);
//        createDrugParentInstanceYCard(doDownloadOfAllandEXTRACTION);
        return doDownloadOfAllandEXTRACTION;
    }

    public void initDB() {
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        db = dbFactory.newEmbeddedDatabase(db_file);
    }

    String startIntegrateOneFolder(HashMap<String, HashSet<String>> drug_folder, String drug_name) throws FileNotFoundException, IOException {

        String res = "";
        try (Transaction tx = db.beginTx()) {
            Tools.startTime();
            HashMap<Integer, String> ordering_paths = new HashMap<>();
            for (String string1 : drug_folder.get(drug_name)) {
                if (string1.contains(Properties.ycs_basic_end_event)) {
                    ordering_paths.put(2, string1);
                }
                if (string1.contains(Properties.ycs_basic_end_case)) {
                    ordering_paths.put(0, string1);
                }
                if (string1.contains(Properties.ycs_basic_end_drug)) {
                    ordering_paths.put(1, string1);
                }
            }

            String reader_case = ordering_paths.get(0);
            String reader_drug = ordering_paths.get(1);
            String reader_event = ordering_paths.get(2);

            BufferedReader br_case = new BufferedReader(new FileReader(reader_case));

            Iterator<String> it = br_case.lines().iterator();
            it.next(); // First Line only props...

            while (it.hasNext()) {
                String next = it.next();
                String[] split_case = next.split(",");
                String key = präfix_ycs + drug_name + split_case[0];
                Node node_report = db.createNode(Entities.Report);
                Node node_patient = db.createNode(Entities.Patient);
                Node node_outcome = db.createNode(Entities.Outcome);
                for (Map.Entry<String, Integer> entry : get_reports.entrySet()) {
                    switch (entry.getValue()) {
                        case -2:
                            node_report.setProperty(entry.getKey(), key);
                            break;
                        case -1:
                            node_report.setProperty(entry.getKey(), "n/a");
                            break;
                        default:
                            node_report.setProperty(entry.getKey(), split_case[entry.getValue()]);
                            break;
                    }
                    node_report.setProperty(Properties.UNIQUE_SOURCE, Properties.unique_id_uk);
                }

                for (Map.Entry<String, Integer> entry : get_patients.entrySet()) {
                    switch (entry.getValue()) {
                        case -2:
                            node_patient.setProperty(entry.getKey(), key);
                            break;
                        case -1:
                            node_patient.setProperty(entry.getKey(), "n/a");
                            break;
                        default:
                            node_patient.setProperty(entry.getKey(), split_case[entry.getValue()]);
                            break;
                    }
                }

                for (Map.Entry<String, Integer> entry : get_outcome.entrySet()) {
                    switch (entry.getValue()) {
                        case -1:
                            node_outcome.setProperty(entry.getKey(), "n/a");
                            break;
                        default:
                            node_outcome.setProperty(entry.getKey(), split_case[entry.getValue()]);
                            break;
                    }
                }
                node_report.createRelationshipTo(node_patient, EntitiesRelationships.DESCRIBES_RdP);
                node_patient.createRelationshipTo(node_outcome, EntitiesRelationships.HAS_PhO);
                patient_for_link.put(key, node_patient.getId());
            }
            br_case.close();

            br_case = new BufferedReader(new FileReader(reader_drug));
            it = br_case.lines().iterator();
            it.next(); // First Line only props...

            while (it.hasNext()) {
                String next = it.next();
                String[] split_drug = next.split(",");
                String key = präfix_ycs + drug_name + split_drug[0];
                Node node_drug = db.createNode(Entities.Drug);
                for (Map.Entry<String, Integer> entry : get_drug.entrySet()) {
                    switch (entry.getValue()) {
                        case -2:
                            node_drug.setProperty(entry.getKey(), drug_name.toUpperCase());
                            break;
                        case -1:
                            node_drug.setProperty(entry.getKey(), "n/a");
                            break;
                        default:
                            node_drug.setProperty(entry.getKey(), split_drug[entry.getValue()]);
                            break;
                    }
                }
                Long get_id = patient_for_link.get(key);
                if (get_id != null) {
                    db.getNodeById(get_id).createRelationshipTo(node_drug, EntitiesRelationships.TAKES_PtD);
                }
            }

            br_case = new BufferedReader(new FileReader(reader_event));
            it = br_case.lines().iterator();
            it.next(); // First Line only props...
            while (it.hasNext()) {
                String next = it.next();
                String[] split_event = next.split(",");
                String key = präfix_ycs + drug_name + split_event[0];
                Node node_reaction = db.createNode(Entities.Reaction);
                for (Map.Entry<String, Integer> entry : get_reactions.entrySet()) {
                    switch (entry.getValue()) {
                        case 100:
                            node_reaction.setProperty(entry.getKey(), meddra);
                            break;
                        case -1:
                            node_reaction.setProperty(entry.getKey(), "n/a");
                            break;
                        default:
                            node_reaction.setProperty(entry.getKey(), split_event[entry.getValue()]);
                            break;
                    }
                }
                Long get_id = patient_for_link.get(key);
                if (get_id != null) {
                    db.getNodeById(get_id).createRelationshipTo(node_reaction, EntitiesRelationships.PRESENTS_PpR);
                }
            }
            br_case.close();
            patient_for_link.clear();
            tx.success();
            counter_process++;
            return counter_process + "/" + counter_max + "\t" + Tools.round(counter_process, counter_max, 3) + " %";
        }
    }

    void closeDB() {
        db.shutdown();
    }

    String getMeddraVersion(HashMap<String, HashSet<String>> drug_folder) throws IOException {
        String meddra = "";
        for (String string : drug_folder.keySet()) {
            String drug_name = string;
            for (String string1 : drug_folder.get(drug_name)) {
                // INFO file..
                if (string1.contains(Properties.ycs_basic_end_info)) {
                    BufferedReader br = new BufferedReader(new FileReader(string1));
                    Iterator<String> it = br.lines().iterator();
                    it.next(); // First Line only props...
                    int i = 0;
                    while (it.hasNext()) {
                        String[] split = it.next().split(",");
                        if (i == 3) {
                            meddra = split[1];
                            break;
                        }
                        i++;
                    }
                    br.close();
                }
            }
        }
        return meddra;
    }

    HashMap<String, Integer> getMapFor(String mapping_for, HashMap<String, HashMap<String, Integer>> map) {
        return map.get(mapping_for);
    }

    HashMap<Integer, String> parsePropGenerall(Iterator<String> iterator) {
        HashMap<Integer, String> temp_props = new HashMap<>();
        Pattern p_prop = Pattern.compile(pattern_property);
        Matcher m_prop;

        while (iterator.hasNext()) {
            String prop_line = iterator.next();
            m_prop = p_prop.matcher(prop_line);
            if (m_prop.find()) {
                pattern_finisher = Integer.parseInt(m_prop.group(1));
                if (temp_props.containsKey(pattern_finisher)) {
                    break;
                } else {
                    temp_props.put(pattern_finisher, m_prop.group(3));
                }
            }
        }
        return temp_props;
    }

    void doParsingOfProperties() throws IOException {
        drug_case_properties = parseProps(Properties.ycs_prop_case);
        drug_event_properties = parseProps(Properties.ycs_prop_event);
        drug_drug_properties = parseProps(Properties.ycs_prop_drug);
        drug_info_properties = parseProps(Properties.ycs_prop_info);
        drug_summary_properties = parseProps(Properties.ycs_prop_summary);
    }

    int returnPos(String[] arr, String string_original) {
        int retu = 0;
        for (int i = 0; i < arr.length; i++) {
            String string = arr[i].toLowerCase();
            if (string.equals(string_original)) {
                return i;
            }
        }
        if (string_original.equals("")) {
            return -2;
        }
        return -1;
    }

    HashMap<String, HashMap<String, Integer>> creatingAllPropsMapping() throws FileNotFoundException, IOException {
        BufferedReader bbr;
        bbr = new BufferedReader(new FileReader(
                new File(path_c_all)));
        Iterator<String> iterator_all = bbr.lines().iterator();
        HashMap<String, HashMap<String, Integer>> infooos = new HashMap();
        HashMap<String, Integer> one_entity = new HashMap<>();

        while (iterator_all.hasNext()) {
            String next = iterator_all.next();
            switch (next) {
                case "Patient":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String ycs_string = split[4];
                        Integer rightPosition = returnPos(drug_case_properties, ycs_string);
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Patient", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Report":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String ycs_string = split[4];
                        Integer rightPosition = returnPos(drug_case_properties, ycs_string);
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Report", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Outcome":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String ycs_string = split[4];
                        Integer rightPosition = returnPos(drug_case_properties, ycs_string);
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Outcome", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Drug":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String ycs_string = split[4];
                        Integer rightPosition = returnPos(drug_drug_properties, ycs_string);
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Drug", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Reaction":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String ycs_string = split[4];
                        Integer rightPosition = returnPos(drug_event_properties, ycs_string);
                        Integer rPosition = returnPos(drug_info_properties, ycs_string);
                        one_entity.put(original, rightPosition);
                        if (rPosition != -1) {
                            one_entity.put(original, 100); // 100 code for Meddra_Version.
                        }
                        next = iterator_all.next();
                    }
                    infooos.put("Reaction", one_entity);
                    one_entity = new HashMap<>();
                    break;
            }
        }
        bbr.close();
        return infooos;
    }

    String[] parseProps(String what_to_parse) throws FileNotFoundException, IOException {
        String[] props_array = null;
        BufferedReader br = new BufferedReader(new FileReader(rel_path_props));
        Iterator<String> iterator = br.lines().iterator();
        int flag = 0;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(what_to_parse)) {
                flag++;
            }
            if (flag == 2) {
                HashMap<Integer, String> parsePropGenerall = parsePropGenerall(iterator);
                props_array = new String[parsePropGenerall.size()];
                for (int i = 0; i < props_array.length; i++) {
                    props_array[i] = parsePropGenerall.get(i + 1);
                }
                break;
            }
        }
        br.close();

        return props_array;
    }

    HashMap<String, HashSet<String>> doDownloadOfAllandEXTRACTION(HashMap<String, String> all_links, String download_path) throws IOException {

        HashMap<String, HashSet<String>> drug_folder = new HashMap<>();
        for (Map.Entry<String, String> entry : all_links.entrySet()) {
            String name = entry.getKey().replaceAll("\\/", "_");
            String createDir = createDir(download_path, name);
            rel_path_props = createDir;
            String url_id = Properties.ycs_basic_url + entry.getValue();
            String zip_file_only = entry.getValue().replaceAll("/UK_EXTERNAL/NONCOMBINED/", "");
            String relative_ID = zip_file_only.replaceAll("UK_NON_", "").replaceAll(".zip", "");
            String rel_path_zip_file = createDir + symb + zip_file_only;
            if (!new File(rel_path_zip_file).exists()) {
                Tools.doDownload(url_id, rel_path_zip_file);
                Tools.doExtraction(rel_path_zip_file, createDir + symb);
            }
            HashSet<String> all_file_to_parse = new HashSet<>();
            all_file_to_parse.add(createDir + symb + Properties.ycs_basic_beginn + relative_ID + Properties.ycs_basic_end_drug);
            all_file_to_parse.add(createDir + symb + Properties.ycs_basic_beginn + relative_ID + Properties.ycs_basic_end_event);
            all_file_to_parse.add(createDir + symb + Properties.ycs_basic_beginn + relative_ID + Properties.ycs_basic_end_case);
            all_file_to_parse.add(createDir + symb + Properties.ycs_basic_beginn + relative_ID + Properties.ycs_basic_end_info);
            all_file_to_parse.add(createDir + symb + Properties.ycs_basic_beginn + relative_ID + Properties.ycs_basic_end_summary);
            drug_folder.put(name, all_file_to_parse);
        }
        String summary = "mhra_idap_csv_help.txt";
        rel_path_props = rel_path_props + Tools.OSValidator() + summary;
        return drug_folder;
    }

    /**
     *
     * @param links_path
     * @return
     * @throws FileNotFoundException
     */
    HashMap<String, String> getAllLinks(String path, String links_path) throws FileNotFoundException {
        HashMap<String, String> all_drugname_to_link = new HashMap<>();
        BufferedReader bf = new BufferedReader(new FileReader(links_path));
        Iterator<String> iterator = bf.lines().iterator();
        Pattern p = Pattern.compile(drug_name_pattern);
        Pattern p_l = Pattern.compile(drug_link_pattern);
        Matcher m;
        Matcher m_l;
        while (iterator.hasNext()) {
            String linke = iterator.next();
            m = p_l.matcher(linke);
            if (m.find()) {
                String drug_name = m.group(5);
                String next_line_link_id = iterator.next();
                m_l = p.matcher(next_line_link_id);
                if (m_l.find()) {
                    String link_id = m_l.group(5);
                    all_drugname_to_link.put(link_id, drug_name);
                }
            }
        }
        // get properties.. //
        for (String string : all_drugname_to_link.keySet()) {
            rel_path_props = createDir(path, string) + symb + Properties.ycs_help_data;
            break;
        }
        // end //
        return all_drugname_to_link;
    }

    void createDir() {
        File directory = new File(rel_path);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    String createDir(String path, String name) {
        File directory = new File(path + symb + name);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory.getAbsolutePath();
    }

    String doDownloadAndExtract(String jason_file_url, String download_path) throws IOException {
        String output = download_path;
        download_path = download_path + symb + "links.zip";
        if (!new File(download_path).exists()) {
            Tools.doDownload(jason_file_url, download_path);
        }
        String raw_file = Tools.doExtraction(download_path, output);
        return raw_file;
    }

}
