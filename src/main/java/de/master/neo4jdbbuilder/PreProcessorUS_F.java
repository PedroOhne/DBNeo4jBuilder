package de.master.neo4jdbbuilder;

import static de.master.neo4jdbbuilder.Properties.basic_path_database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
public final class PreProcessorUS_F implements Properties {

    GraphDatabaseService db;

    static ConcurrentHashMap<String, String> db_ids;

    static HashSet<USgenerellNode> alls = new HashSet<>();
    static HashMap<String, ArrayList<USadvancedNode>> omegaDrugs_FINAL = new HashMap<>();

    static HashMap<String, ArrayList<String>> omegaDRUGS = new HashMap<>();
    static HashMap<String, ArrayList<String>> omegaREACTIONS = new HashMap<>();
    static HashMap<String, ArrayList<String>> omegaOUTCOMES = new HashMap<>();
    static HashMap<String, ArrayList<String>> omegaREPORSOURCES = new HashMap<>();
    static HashMap<String, ArrayList<String>> omegaTHERAS = new HashMap<>();
    static HashMap<String, ArrayList<String>> omegaINDIS = new HashMap<>();

    private String[] property_arry_drugs;
    private String[] property_arry_indis;
    private String[] property_arry_react;
    private String[] property_arry_report;
    private String[] property_arry_theras;
    private String[] property_arry_outcomes;
    private String[] property_arry_demos;

    static HashMap<String, HashMap<String, Integer>> alls_config_props;

    static HashMap<String, String[]> fusion_standard_properties = new HashMap<>();

    private static HashMap<Integer, String[]> all_properties_contained = new HashMap<>();

    public void init_prop_map() {
        all_properties_contained.put(0, property_arry_demos);
        all_properties_contained.put(1, property_arry_drugs);
        all_properties_contained.put(2, property_arry_react);
        all_properties_contained.put(3, property_arry_theras);
        all_properties_contained.put(4, property_arry_indis);
        all_properties_contained.put(5, property_arry_outcomes);
        all_properties_contained.put(6, property_arry_report);
        for (Integer integer : all_properties_contained.keySet()) {
            for (int i = 0; i < all_properties_contained.get(integer).length; i++) {
                System.out.println(i + "\t" + all_properties_contained.get(integer)[i]);
            }
        }
    }

    public void closeDB() {
        db.shutdown();
    }

    void initFile(File f) {
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        String symb = Tools.OSValidator();
        String rel_path = basic_path_database + symb + basic_path_us + symb;
        db = dbFactory.newEmbeddedDatabase(f);
    }
    
    void setDB_ID(ConcurrentHashMap<String,String> mp){
        this.db_ids = mp;
    }

    public void PreProcessorUS_F_14Q2(SortedSet<String> files_names, int option) throws IOException, InterruptedException {
        clearAllMaps();
        String symb = Tools.OSValidator();

        System.out.println("STARTING ...");

        String drug = "";
        String indi = "";
        String thera = "";
        String reac = "";
        String demo = "";
        String outc = "";
        String source = "";

        Object[] toArray = files_names.toArray();
        demo = (String) toArray[0];
        drug = (String) toArray[1];
        indi = (String) toArray[2];
        outc = (String) toArray[3];
        reac = (String) toArray[4];
        source = (String) toArray[5];
        thera = (String) toArray[6];

        property_arry_drugs = initialize(new File(drug).getAbsolutePath(), omegaDRUGS);
        property_arry_indis = initialize(new File(indi).getAbsolutePath(), omegaINDIS);
        property_arry_theras = initialize(new File(thera).getAbsolutePath(), omegaTHERAS);
        initializeDrugNodes();
        property_arry_react = initialize(new File(reac).getAbsolutePath(), omegaREACTIONS);
        property_arry_report = initialize(new File(source).getAbsolutePath(), omegaREPORSOURCES);
        property_arry_outcomes = initialize(new File(outc).getAbsolutePath(), omegaOUTCOMES);
        reUnion(symb, demo);
        alls_config_props = Tools.ConfigParserStructureGenerall(files_names, option);
        max = alls.size();
        counter = 0;
//        createDATABASE(alls);
//        db.shutdown();
//        clearAllMaps();
    }

    HashSet<USgenerellNode> receiveMapInternNodes() {
        return alls;
    }

    public void clearAllMaps() {
        omegaDRUGS.clear();
        omegaINDIS.clear();
        omegaOUTCOMES.clear();
        omegaREACTIONS.clear();
        omegaREPORSOURCES.clear();
        omegaTHERAS.clear();
        omegaDrugs_FINAL.clear();
        alls.clear();
    }

    void downloadFileToDestination(String url, String output_path) throws IOException {
        Tools.doDownload(url, output_path);
    }

    void extractFileAfterDownload(String zipFile, String outputFolder) {
        Tools.doExtraction(zipFile, outputFolder);
    }

    SortedSet<String> initializePathsAfterDownload(String path, String kind) throws IOException {
        SortedSet<String> all_file_paths = new TreeSet<>();
        // Retrieve download directory, where the txt files are.
        String symb = Tools.OSValidator();
        String directory = Tools.CurrenDirectory();
        Stream<Path> list = Files.list(Paths.get(path));
        for (Object object : list.toArray()) {
            Path p = (Path) object;
            String filename = p.getFileName().toString();
            if (filename.contains(kind)) {
                all_file_paths.add(path + filename);
            }
        }
        return all_file_paths;
    }

    public void fixWeirdDrugnames() {
        String regex = "([^\\x20]+)([\\x20]+)([\\/]{1}[\\d]*[\\/]{1})";
        Pattern pattern_regex = Pattern.compile(regex);
        Matcher m;

        try (Transaction tx = db.beginTx()) {
            ResourceIterator<Node> drugs = db.findNodes(Entities.Drug);
            while (drugs.hasNext()) {
                Node drug_node = drugs.next();
                String property_drug_name = drug_node.getProperty("drugname").toString();
                String newProperty = "";
                m = pattern_regex.matcher(property_drug_name);
                if (m.matches()) {
                    drug_node.removeProperty("drugname");
                    newProperty = m.group(1);
                    drug_node.setProperty("drugname", newProperty);
                }
            }
            tx.success();
        }
//        db.shutdown();
    }

    static int counter = 0;
    static int max;

    public String ProcessOneNode(USgenerellNode node) {
        USThread_F_14Q2 thread = new USThread_F_14Q2(node, alls_config_props, db, db_ids);
        thread.run();
        counter++;
        return counter + "/" + max + "\t\t" + Tools.round(counter, max, 3) + "  %";
    }

    public void reUnion(String symb, String Demographic) throws IOException {
        String rel_path = basic_path_database + symb + basic_path_us + symb;
        BufferedReader bf = new BufferedReader(new FileReader(new File(Demographic).getAbsolutePath()));
        Iterator<String> iterator = bf.lines().iterator();
        String next = iterator.next();

        String[] first_split = Tools.splitOneLine(next);
        String[] expandArray = Tools.expandArray(first_split);
        String[] newArray = Tools.expandArray(expandArray);
        property_arry_demos = newArray;
        int i = 0;
        for (String string : first_split) {
            property_arry_demos[i] = string;
            i++;
        }
        property_arry_demos[i] = Properties.fusion_canada_height;
        property_arry_demos[i + 1] = Properties.fusion_canada_height_cod;
        System.out.println(property_arry_demos.length);

        while (iterator.hasNext()) {
            String next1 = iterator.next();
            String key_prime = Tools.splitOneLine(next1)[0];
            ArrayList<USadvancedNode> d_node_set = omegaDrugs_FINAL.get(key_prime);
            ArrayList<String> rset = omegaREACTIONS.get(key_prime);
            ArrayList<String> oset = omegaOUTCOMES.get(key_prime);
            ArrayList<String> rsset = omegaREPORSOURCES.get(key_prime);
            USgenerellNode n = new USgenerellNode(key_prime, next1, d_node_set, property_arry_demos);
            n.addReaction(rset);
            if (oset instanceof ArrayList) {
                n.addOutcomes(oset);
            }
            if (rsset instanceof ArrayList) {
                n.addReportSources(rsset);
            }
            alls.add(n);
        }
        bf.close();
    }

    static void initializeDrugNodes() throws IOException {
        for (String string : omegaDRUGS.keySet()) {
            ArrayList<USadvancedNode> ha = new ArrayList<>();
            for (String string1 : omegaDRUGS.get(string)) {
                ArrayList<String> indi_to_drugs = new ArrayList<>();
                ArrayList<String> thera_to_drugs = new ArrayList<>();
                String drug_sec_id = Tools.splitToSecondary(string1);
                USadvancedNode anode = new USadvancedNode(string, drug_sec_id, string1);

                ArrayList<String> indis = omegaINDIS.get(string);
                if (indis instanceof ArrayList) {
                    for (String indi : indis) {
                        String second_key = Tools.splitToSecondary(indi);
                        if (drug_sec_id.equals(second_key)) {
                            indi_to_drugs.add(indi);
                        }
                    }
                    anode.setIndications(indi_to_drugs);
                }

                ArrayList<String> theras = omegaTHERAS.get(string);
                if (theras instanceof ArrayList) {
                    for (String thera : theras) {
                        String second_key = Tools.splitToSecondary(thera);
                        if (drug_sec_id.equals(second_key)) {
                            thera_to_drugs.add(thera);
                        }
                    }
                    anode.setTherapies(thera_to_drugs);
                }
                ha.add(anode);
            }
            omegaDrugs_FINAL.put(string, ha);
        }
        omegaINDIS.clear();
        omegaTHERAS.clear();
        omegaDRUGS.clear();
    }

    String[] initialize(String what, HashMap<String, ArrayList<String>> hm) throws FileNotFoundException, IOException {
        System.out.println("Set Up Phase..");
        ArrayList<String> h_set = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new FileReader(what));
        Iterator<String> iterator = bf.lines().iterator();
        String next = iterator.next();
        String[] properties_first_line = Tools.splitOneLine(next);
        String[] new_array = new String[properties_first_line.length];
        String changed = "";

        if (properties_first_line[2].equals("dsg_drug_seq") | properties_first_line[2].equals("indi_drug_seq")) {
            changed = "drug_seq";
        }

        if (!changed.equals("")) {
            for (int i = 0; i < new_array.length; i++) {
                new_array[i] = properties_first_line[i];
            }
            new_array[2] = changed;
        } else {
            for (int i = 0; i < new_array.length; i++) {
                new_array[i] = properties_first_line[i];
            }
        }

        String next1 = iterator.next();
        h_set.add(next1);
        String[] split = Tools.splitOneLineFIRST_TIME(next1);
        String first = split[0];

        while (iterator.hasNext()) {
            String line = iterator.next();
            String key = Tools.splitOneLineFIRST_TIME(line)[0];
            if (key.equals(first)) {
                h_set.add(line);
            } else {
                hm.put(first, h_set);
                h_set = new ArrayList<>();
                h_set.add(line);
                first = key;
            }
        }
        hm.put(first, h_set);
        bf.close();
        return new_array;
    }

    static HashMap<String, String[]> returnFusionsPropertiesFromFEARS() {
        return fusion_standard_properties;
    }

}
