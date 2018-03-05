package de.master.neo4jdbbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
public final class PreProcessorCanada_F implements Properties {

    GraphDatabaseService db;

    static HashMap<String, String[]> drug_product_IDs = new HashMap<>();
    static HashMap<String, ArrayList<String>> drug_product_ingri_IDs = new HashMap<>();
    static HashMap<String, ArrayList<String>> report_drug_IDs = new HashMap<>();
    static HashMap<String, ArrayList<String>> report_drug_indication_IDs = new HashMap<>();

    static HashMap<String, ArrayList<String>> reactions_IDs = new HashMap<>();
    static HashMap<String, ArrayList<String>> links_IDs = new HashMap<>();

    static HashMap<String, String[]> reports_IDs = new HashMap<>();

    static HashSet<CanadaNode> all_entries = new HashSet<>();
    static HashSet<CanadaExtendedNode> drug_prd_ALL = new HashSet<>();
    static HashMap<String, CanadaExtendedNode> createD_Product_Nodes = new HashMap<>();

    private static String[] property_arry_report;
    private static String[] property_arry_react;
    private static String[] property_arry_report_drug;
    private static String[] property_arry_report_drug_indication;
    private static ArrayList<String> propperty_reports = new ArrayList<>();
    private static ArrayList<String> propperty_reactions = new ArrayList<>();
    private static ArrayList<String> propperty_drugs = new ArrayList<>();
    private static ArrayList<String> propperty_drugs_indication = new ArrayList<>();

    static HashMap<String, HashMap<String, Integer>> my_props = new HashMap<>();
    static HashMap<Integer, String[]> all_canadian_props = new HashMap<>();

    public PreProcessorCanada_F(File f, String download_path, String unzipped_folder) throws IOException {
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        db = dbFactory.newEmbeddedDatabase(f);
        my_props = createPropertyMap();
        CreatePreprocessingCanada(download_path, unzipped_folder);
        createDATABASE();
    }

    public HashMap<String, HashMap<String, Integer>> createPropertyMap() throws FileNotFoundException, IOException {

        String config_path_dir1 = Properties.config_path_dir;
        String OSValidator = Tools.OSValidator();
        File f = new File(config_path_dir1 + OSValidator + Properties.canada_own_property_file);
        HashMap<String, Integer> canada_props = new HashMap<>();
        HashMap<Integer, HashMap<String, Integer>> all_canada_props = new HashMap();

        if (f.exists()) { // reading properties from pre_canada.txt
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            Iterator<String> iterator = bfr.lines().iterator();

            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.startsWith("PROPERTY 0")) {
                    while (!next.equals("")) {
                        String[] split = next.split("\\$");
                        if (split.length == 2) {// first number [0], then attribute [1]
                            canada_props.put(split[1], Integer.valueOf(split[0]));
                            propperty_reports.add(split[1]);
                        } else {
                        }
                        next = iterator.next();
                    }
                    all_canada_props.put(0, canada_props);
                    canada_props = new HashMap<>();
                }
            }

            bfr.close();
            bfr = new BufferedReader(new FileReader(f));
            iterator = bfr.lines().iterator();

            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.startsWith("PROPERTY 1")) {
                    while (!next.equals("")) {
                        String[] split = next.split("\\$");
                        if (split.length == 2) {// first number [0], then attribute [1]
                            canada_props.put(split[1], Integer.valueOf(split[0]));
                            propperty_reactions.add(split[1]);
                        } else {
                        }
                        next = iterator.next();
                    }
                    all_canada_props.put(1, canada_props);
                    canada_props = new HashMap<>();
                }
            }

            bfr.close();
            bfr = new BufferedReader(new FileReader(f));
            iterator = bfr.lines().iterator();

            while (iterator.hasNext()) {
                String next = iterator.next();

                if (next.startsWith("PROPERTY 2")) {
                    while (!next.equals("")) {
                        String[] split = next.split("\\$");
                        if (split.length == 2) {// first number [0], then attribute [1]
                            canada_props.put(split[1], Integer.valueOf(split[0]));
                            propperty_drugs.add(split[1]);
                        } else {
                        }
                        next = iterator.next();
                    }
                    all_canada_props.put(2, canada_props);
                    canada_props = new HashMap<>();
                }
            }

            bfr.close();
            bfr = new BufferedReader(new FileReader(f));
            iterator = bfr.lines().iterator();

            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.startsWith("PROPERTY 3")) {
                    while (iterator.hasNext()) {
                        String[] split = next.split("\\$");
                        if (split.length == 2) {// first number [0], then attribute [1]
                            canada_props.put(split[1], Integer.valueOf(split[0]));
                            propperty_drugs_indication.add(split[1]);
                        } else {
                        }
                        next = iterator.next();
                    }
                    all_canada_props.put(3, canada_props);
                    canada_props = new HashMap<>();
                }
            }

            property_arry_report = new String[propperty_reports.size()];
            for (int i = 0; i < property_arry_report.length; i++) {
                property_arry_report[i] = propperty_reports.get(i);
            }
            property_arry_react = new String[propperty_reactions.size()];
            for (int i = 0; i < property_arry_react.length; i++) {
                property_arry_react[i] = propperty_reactions.get(i);
            }
            property_arry_report_drug = new String[propperty_drugs.size()];
            for (int i = 0; i < property_arry_report_drug.length; i++) {
                property_arry_report_drug[i] = propperty_drugs.get(i);
            }
            property_arry_report_drug_indication = new String[propperty_drugs_indication.size()];
            for (int i = 0; i < property_arry_report_drug_indication.length; i++) {
                property_arry_report_drug_indication[i] = propperty_drugs_indication.get(i);
            }
        } else {
            HashMap<String, String[]> data_to_properties;
            try {
                data_to_properties = Tools.featchDATAandPROPS(canada_property_url);
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                int i = 0;
                for (String string : data_to_properties.keySet()) {
                    switch (string) {
                        case "Reports.txt":
                            property_arry_report = data_to_properties.get(string);
                            bw.write("PROPERTY 0\n");
                            i = 0;
                            for (String string1 : property_arry_report) {
                                bw.write(i + "$" + string1 + "\n");
                                canada_props.put(string1, i);
                                i++;
                            }
                            all_canada_props.put(0, canada_props);
                            canada_props = new HashMap<>();
                            bw.write("\n");
                            break;
                    }
                }

                for (String string : data_to_properties.keySet()) {
                    i = 0;
                    switch (string) {
                        case "Reactions.txt":
                            property_arry_react = data_to_properties.get(string);
                            bw.write("PROPERTY 1\n");
                            i = 0;
                            for (String string1 : property_arry_react) {
                                bw.write(i + "$" + string1 + "\n");
                                canada_props.put(string1, i);
                                i++;
                            }
                            all_canada_props.put(1, canada_props);
                            canada_props = new HashMap<>();
                            bw.write("\n");
                            break;
                    }
                }

                for (String string : data_to_properties.keySet()) {
                    i = 0;
                    switch (string) {
                        case "Report_Drug.txt":
                            property_arry_report_drug = data_to_properties.get(string);
                            bw.write("PROPERTY 2\n");
                            i = 0;
                            for (String string1 : property_arry_report_drug) {
                                bw.write(i + "$" + string1 + "\n");
                                canada_props.put(string1, i);
                                i++;
                            }
                            all_canada_props.put(2, canada_props);
                            canada_props = new HashMap<>();
                            bw.write("\n");
                            break;
                    }
                }

                for (String string : data_to_properties.keySet()) {
                    i = 0;
                    switch (string) {
                        case "Report_Drug_Indication.txt":
                            property_arry_report_drug_indication = data_to_properties.get(string);
                            bw.write("PROPERTY 3\n");
                            i = 0;
                            for (String string1 : property_arry_report_drug_indication) {
                                bw.write(i + "$" + string1 + "\n");
                                canada_props.put(string1, i);
                                i++;
                            }
                            all_canada_props.put(3, canada_props);
                            canada_props = new HashMap<>();
                            bw.write("\n");
                            break;
                    }
                }

                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(PreProcessorCanada_F.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        File config_all = new File(config_path_dir1 + OSValidator + Properties.config_path_file);
        BufferedReader bfr_all = new BufferedReader(new FileReader(config_all));
        Iterator<String> iterator_all = bfr_all.lines().iterator();
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
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(0));
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
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(0));
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
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(0));
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Outcome", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Source":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(0));
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Source", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Drug":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(2));
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Drug", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Therapy":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(2));
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Therapy", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Indication":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(3));
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Indication", one_entity);
                    one_entity = new HashMap<>();
                    break;
                case "Reaction":
                    iterator_all.next();
                    next = iterator_all.next();
                    while (!next.equals("")) {
                        String[] split = next.split("\\$", -1);
                        String original = split[0];
                        String canada_string = split[3];
                        Integer rightPosition = returnPositionInArray(canada_string, all_canada_props.get(1));
                        one_entity.put(original, rightPosition);
                        next = iterator_all.next();
                    }
                    infooos.put("Reaction", one_entity);
                    one_entity = new HashMap<>();
                    break;
            }
        }

        return infooos;
    }

    public Integer returnPositionInArray(String original, HashMap<String, Integer> a) {
        for (Map.Entry<String, Integer> entry : a.entrySet()) {
            if (entry.getKey().equals(original)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Creating intern java objects containing all informations connected for
     * each set. For Example: One Patient has One Report, that Patient has Many
     * Reactions and Many Drugs etc...
     *
     * @throws IOException
     */
    public void CreatePreprocessingCanada(String download_path, String unzipped_folder) throws IOException {

        // Retrieve download directory, where the txt files are.
        String symb = Tools.OSValidator();
        String path = download_path + symb + unzipped_folder;
        Stream<Path> list = Files.list(Paths.get(path));

        // Initialise Report/Reaction/Drug/DrugIndication Maps
        for (Object object : list.toArray()) {
            Path a = (Path) object;
            String filename = a.getFileName().toString();
            if (filename.equals("reactions.txt")) {
                initialize(new File(path + filename).getAbsolutePath(), reactions_IDs, 1);
            } else if (filename.equals("report_drug.txt")) {
                initialize(new File(path + filename).getAbsolutePath(), report_drug_IDs, 1);
            } else if (filename.equals("report_drug_indication.txt")) {
                initialize(new File(path + filename).getAbsolutePath(), report_drug_indication_IDs, 1);
            } else if (filename.equals("reports.txt")) {
                initializeOneToOneS(new File(path + filename).getAbsolutePath(), reports_IDs, 0);
            }
        }

        // Creating "Nodes" containing all relevant connections.
        HashMap<String, CanadaReportDrugNode> createReportDrugNodes = createReportDrugNodes(report_drug_IDs, report_drug_indication_IDs);
        all_entries = createFinalNodes(reports_IDs, reactions_IDs, createReportDrugNodes);
    }

    final void createDATABASE() {
        Iterator<CanadaNode> iterator = all_entries.iterator();
        while (iterator.hasNext()) {
//            Tools.startTime();
            CanadaNode canaNODE = iterator.next();
            CanadaThread_F c = new CanadaThread_F(canaNODE, my_props, db);
            c.run();
//            Tools.printPassedTime(canaNODE.getAer_key());
        }
    }

    /**
     *
     * @param reports All Reports.
     * @param links All Linkages
     * @param reactions All Reactions
     * @param report_drugs All Report_Drugs
     * @return
     */
    static HashSet<CanadaNode> createFinalNodes(HashMap<String, String[]> reports,
            HashMap<String, ArrayList<String>> reactions,
            HashMap<String, CanadaReportDrugNode> report_drugs) {

        HashSet<CanadaNode> ss_bottom = new HashSet<>();

        reports.keySet().stream().map((String string) -> {
            CanadaReportDrugNode get = report_drugs.get(string);
            String[] getProperty = reports.get(string);
            CanadaNode cn = new CanadaNode(string, getProperty, get);
            ArrayList<String> reactionss = reactions.get(string);
            if (reactionss instanceof ArrayList) {
                cn.setReactions(reactionss);
            }
            return cn;
        }).forEachOrdered((cn) -> {
            ss_bottom.add(cn);
        });
        return ss_bottom;
    }

    static HashMap<String, CanadaReportDrugNode> createReportDrugNodes(HashMap<String, ArrayList<String>> hm_parent,
            HashMap<String, ArrayList<String>> hm_childs) {

        HashMap<String, CanadaReportDrugNode> ss_bottom = new HashMap<>();
        hm_parent.keySet().forEach((string) -> {
            CanadaReportDrugNode cen;
            ArrayList<String> parents = hm_parent.get(string);
            String[] split = Tools.returnCanadaProperyOneLineArray(parents.get(0)); // Report_ID (second entry)
            ArrayList<String> childs = hm_childs.get(string);

            if (childs instanceof ArrayList) {
                cen = new CanadaReportDrugNode(split[0], hm_parent.get(string), childs);
            } else {
                cen = new CanadaReportDrugNode(split[0], hm_parent.get(string), null);
            }

            ss_bottom.put(string, cen);
        });
        return ss_bottom;
    }

    static void initialize(String what, HashMap<String, ArrayList<String>> hm, int key_order) throws FileNotFoundException, IOException {
        System.out.println("Set Up Phase..");
        BufferedReader bf = new BufferedReader(new FileReader(what));
        Iterator<String> iterator = bf.lines().iterator();
        List<String> pre_ordering = new ArrayList<String>();

        // sorting entries of file...
        while (iterator.hasNext()) {
            String line = iterator.next();
            String key = Tools.splitOneLineCanada(line)[key_order];
            String result = key + "§" + line;
            pre_ordering.add(result);
        }
        Collections.sort(pre_ordering);
        // sorting finished...

        ArrayList<String> multiple_entry = new ArrayList<>();
        Iterator<String> iterator_after = pre_ordering.iterator();
        String[] split_first = iterator_after.next().split("\\§");
        String first = split_first[0];

        while (iterator_after.hasNext()) {
            String next = iterator_after.next();
            String[] split_after = next.split("\\§");

            if (first.equals(split_after[0])) {
                multiple_entry.add(split_after[1]);
            } else {
                hm.put(first, multiple_entry);
                multiple_entry = new ArrayList<>();
                multiple_entry.add(split_after[1]);
                first = split_after[0];
            }
        }
        bf.close();
    }

    static void initializeOneToOneS(String what, HashMap<String, String[]> hm, int key_order) throws FileNotFoundException, IOException {
        System.out.println("Set Up Phase..");
        BufferedReader bf = new BufferedReader(new FileReader(what));
        Iterator<String> iterator = bf.lines().iterator();
        List<String> pre_ordering = new ArrayList<String>();

        // sorting entries of file...
        while (iterator.hasNext()) {
            String line = iterator.next();
            String key = Tools.splitOneLineCanada(line)[key_order];
            String result = key + "§" + line;
            pre_ordering.add(result);
        }
        Collections.sort(pre_ordering);
        // sorting finished...

        Iterator<String> iterator_after = pre_ordering.iterator();

        while (iterator_after.hasNext()) {
            String next = iterator_after.next();
            String[] split_after = next.split("\\§");
            hm.put(split_after[0], split_after);
        }
        bf.close();
    }

    /**
     * Section -> Methods/Functions which are not needed anymore.
     */
    
    public void ConnectDRUGproducts_to_ReportDRUG() {
        // LONG..ID..Indication   <->   DRUG_PRODCUT_ID ....  
        // First Step.hj
        HashMap<Long, String> first_unordered_map = new HashMap<>();
        try (Transaction tx = db.beginTx()) {
            ResourceIterator<Node> all_indications = db.findNodes(Entities.Indication);
            while (all_indications.hasNext()) {
                Node n = all_indications.next();
                Long key = n.getId();
                String drug_product_prop = n.getProperty("drug_product_id").toString();
                first_unordered_map.put(key, drug_product_prop);
            }
            tx.success();
        }

        // Second Step.
        HashMap<String, HashSet<Long>> first_ordered_map = new HashMap<>();
        for (Map.Entry<Long, String> entry : first_unordered_map.entrySet()) {
            String drug_product_id = entry.getValue();
            Long key_id_indication_node = entry.getKey();
            if (first_ordered_map.containsKey(drug_product_id)) {
                HashSet<Long> get_set = first_ordered_map.get(drug_product_id);
                get_set.add(key_id_indication_node);
                first_ordered_map.put(drug_product_id, get_set);
            } else {
                HashSet<Long> new_set = new HashSet<>();
                new_set.add(key_id_indication_node);
                first_ordered_map.put(drug_product_id, new_set);
            }
        }

        // Third Step. Connect Indication Nodes with DrugProduct.
        int stopper = 0;
        try (Transaction tx = db.beginTx()) {
            for (Map.Entry<String, HashSet<Long>> entry : first_ordered_map.entrySet()) {
                Tools.startTime();
                stopper++;
                Node drug_product_node = db.findNode(Entities.DrugProduct, "drug_product_id", entry.getKey());
                if (drug_product_node instanceof Node) {
                    for (Long long_id_indication : entry.getValue()) {
                        Node indi = db.getNodeById(long_id_indication);
                        indi.createRelationshipTo(drug_product_node, EntitiesRelationships.INDICATION_PRODUCT);
                    }
                } else {
                    System.out.println("No Node for: " + entry.getKey());
                }
                if (stopper % 5000 == 0) {
                    tx.success();
                    db.beginTx();
                }
                Tools.printPassedTime(String.valueOf(entry.getValue().size()));
            }
            tx.success();
        }

        db.shutdown();
    }

}