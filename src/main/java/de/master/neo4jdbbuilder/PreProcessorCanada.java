package de.master.neo4jdbbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
public class PreProcessorCanada implements Properties {

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

    private static String[] property_arry_drugs;
    private static String[] property_arry_drugs_ingriedients;

    private static String[] property_arry_demos;
    private static String[] property_arry_links;
    private static String[] property_arry_react;
    private static String[] property_arry_report_drug;
    private static String[] property_arry_report_drug_indication;
    
    static HashMap<Integer,String[]> all_canadian_props =  new HashMap<>();

    public PreProcessorCanada(File f) throws IOException {
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        db = dbFactory.newEmbeddedDatabase(f);
        CreatePreprocessingCanada();
        createDATABASE();
        Tools.let_DB_rest_ALittleBit(db, 30);
    }

    public void CreatePreprocessingCanada() throws IOException {
        HashMap<String, String[]> data_to_properties = Tools.featchDATAandPROPS(canada_property_url);

        for (String string : data_to_properties.keySet()) {
            switch (string) {
                case "Reports.txt":
                    property_arry_demos = data_to_properties.get(string);
                    break;
                case "Report_Links_LX.txt":
                    property_arry_links = data_to_properties.get(string);
                    break;
                case "Reactions.txt":
                    property_arry_react = data_to_properties.get(string);
                    break;
                case "Report_Drug.txt":
                    property_arry_report_drug = data_to_properties.get(string);
                    break;
                case "Report_Drug_Indication.txt":
                    property_arry_report_drug_indication = data_to_properties.get(string);
                    break;
                case "Drug_Product_Ingredients.txt":
                    property_arry_drugs_ingriedients = data_to_properties.get(string);
                    break;
                case "Drug_Product.txt":
                    property_arry_drugs = data_to_properties.get(string);
                    break;
            }

        }
        all_canadian_props.put(0, property_arry_demos);
        all_canadian_props.put(1, property_arry_drugs);
        all_canadian_props.put(2, property_arry_drugs_ingriedients);
        all_canadian_props.put(3, property_arry_react);
        all_canadian_props.put(4, property_arry_report_drug);
        all_canadian_props.put(5, property_arry_report_drug_indication);
        all_canadian_props.put(6, property_arry_links);

        for (Integer integer : all_canadian_props.keySet()) {
            System.out.println("PROPERTIES "+integer);
            for (String string : all_canadian_props.get(integer)) {
                System.out.println(string);
            }
            System.out.println("END\n");
        }

        SortedSet<String> outsorted_files = new TreeSet<>();

        for (String string : data_to_properties.keySet()) {
            if (string.equals("Report_Links_LX.txt")) {
                string = string.replace("_LX", "");
            }
            if (string.equals("Drug_Product.txt")) {
                string = "Drug_Products.txt";
            }
            if (!string.endsWith("_LX.txt")) {
                outsorted_files.add(string);
            }
        }

        String symb = Tools.OSValidator();
        String directory = Tools.CurrenDirectory();
        String path = directory + symb + basic_path_database + symb + outputFolder + symb;

        for (String outsorted_file : outsorted_files) {

            if (outsorted_file.equals("Drug_Product_Ingredients.txt")) {
                initialize(new File(path + outsorted_file).getAbsolutePath(), drug_product_ingri_IDs, 1);
            } else if (outsorted_file.equals("Drug_Products.txt")) {
                initializeOneToOne(new File(path + outsorted_file).getAbsolutePath(), drug_product_IDs, 0);
            } else if (outsorted_file.equals("Reactions.txt")) {
                initialize(new File(path + outsorted_file).getAbsolutePath(), reactions_IDs, 1);
            } else if (outsorted_file.equals("Report_Drug.txt")) {
                initialize(new File(path + outsorted_file).getAbsolutePath(), report_drug_IDs, 1);
            } else if (outsorted_file.equals("Report_Drug_Indication.txt")) {
                initialize(new File(path + outsorted_file).getAbsolutePath(), report_drug_indication_IDs, 1);
            } else if (outsorted_file.equals("Report_Links.txt")) {
                initialize(new File(path + outsorted_file).getAbsolutePath(), links_IDs, 1);
            } else if (outsorted_file.equals("Reports.txt")) {
                initializeOneToOneS(new File(path + outsorted_file).getAbsolutePath(), reports_IDs, 0);
            }
        }

        HashMap<String, CanadaReportDrugNode> createReportDrugNodes = createReportDrugNodes(report_drug_IDs, report_drug_indication_IDs); // REPORT_DRUG (indication)
        all_entries = createFinalNodes(reports_IDs, links_IDs, reactions_IDs, createReportDrugNodes);
    }

    public void createRestDB() {
        HashMap<String, CanadaExtendedNode> drug_products = createD_Product_Nodes(drug_product_IDs, drug_product_ingri_IDs);
        Iterator<CanadaExtendedNode> ati = drug_products.values().iterator();
        while (ati.hasNext()) {
            CanadaExtendedNode canaNODE = ati.next();
            CanadaThreadREST c = new CanadaThreadREST(canaNODE, all_canadian_props, db);
            c.run();
        }
    }

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
            System.out.println(first_unordered_map.size());
            tx.success();
        }
        System.out.println("All Indications...:\t" + first_unordered_map.size());

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
        System.out.println("All DrugProducts...:\t" + first_ordered_map.size());

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

//        db.shutdown();
    }

    public void createDATABASE() {
        Iterator<CanadaNode> iterator = all_entries.iterator();
        while (iterator.hasNext()) {
            CanadaNode canaNODE = iterator.next();
            CanadaThread c = new CanadaThread(canaNODE, all_canadian_props, db);
            c.run();
        }

        createRestDB(); // Nachträgliches Hinzufügen in die Report Drug Nodes....
        ConnectDRUGproducts_to_ReportDRUG();
    }

    /**
     *
     *
     *
     * @param reports All Reports.
     * @param links All Linkages
     * @param reactions All Reactions
     * @param report_drugs All Report_Drugs
     * @return
     */
    static HashSet<CanadaNode> createFinalNodes(HashMap<String, String[]> reports,
            HashMap<String, ArrayList<String>> links, HashMap<String, ArrayList<String>> reactions,
            HashMap<String, CanadaReportDrugNode> report_drugs) {

        HashSet<CanadaNode> ss_bottom = new HashSet<>();

        for (String string : reports.keySet()) {
            CanadaReportDrugNode get = report_drugs.get(string);
            String[] getProperty = reports.get(string);
            CanadaNode cn = new CanadaNode(string, getProperty, get);
            ArrayList<String> linkages = links.get(string);
            ArrayList<String> reactionss = reactions.get(string);
            if (linkages instanceof ArrayList) {
                cn.setLinks(linkages);
            }
            if (reactionss instanceof ArrayList) {
                cn.setReactions(reactionss);
            }

            ss_bottom.add(cn);
        }
        return ss_bottom;
    }

    static HashMap<String, CanadaReportDrugNode> createReportDrugNodes(HashMap<String, ArrayList<String>> hm_parent,
            HashMap<String, ArrayList<String>> hm_childs) {

        HashMap<String, CanadaReportDrugNode> ss_bottom = new HashMap<>();
        for (String string : hm_parent.keySet()) {
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

        }
        return ss_bottom;
    }

    static HashMap<String, CanadaExtendedNode> createD_Product_Nodes(HashMap<String, String[]> hm_drug_product, HashMap<String, ArrayList<String>> hm_d_p_ingridients) {
        HashMap<String, CanadaExtendedNode> ss_bottom = new HashMap<>();
        for (String string : hm_drug_product.keySet()) {
            CanadaExtendedNode cen;
            ArrayList<String> childs = hm_d_p_ingridients.get(string);
            if (childs instanceof ArrayList) {
                cen = new CanadaExtendedNode(string, hm_drug_product.get(string));
                cen.setDrugProductIngridients(childs);
            } else {
                cen = new CanadaExtendedNode(string, hm_drug_product.get(string));
            }
            ss_bottom.put(string, cen);
        }
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
            String result = key + "?" + line;
            pre_ordering.add(result);
        }
        Collections.sort(pre_ordering);
        // sorting finished...

        ArrayList<String> multiple_entry = new ArrayList<>();
        Iterator<String> iterator_after = pre_ordering.iterator();
        String[] split_first = iterator_after.next().split("\\?");
        String first = split_first[0];

        while (iterator_after.hasNext()) {
            String next = iterator_after.next();
            String[] split_after = next.split("\\?");

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

    static void initializeOneToOne(String what, HashMap<String, String[]> hm, int key_order) throws FileNotFoundException, IOException {
        System.out.println("Set Up Phase..");
        BufferedReader bf = new BufferedReader(new FileReader(what));
        Iterator<String> iterator = bf.lines().iterator();
        List<String> pre_ordering = new ArrayList<String>();

        // sorting entries of file...
        while (iterator.hasNext()) {
            String line = iterator.next();
            String key = Tools.splitOneLineCanada(line)[key_order];
            String result = key + "?" + line;
            pre_ordering.add(result);
        }
        Collections.sort(pre_ordering);
        // sorting finished...

        Iterator<String> iterator_after = pre_ordering.iterator();

        while (iterator_after.hasNext()) {
            String next = iterator_after.next();
            String[] split_after = next.split("\\?");
            String[] split_after_after = split_after[1].split("\\$");
            String[] mods_final = new String[split_after_after.length];
            for (int i = 0; i < mods_final.length; i++) {
                mods_final[i] = split_after_after[i].replaceAll("\"", "");
            }
            hm.put(split_after[0], mods_final);
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
            String result = key + "?" + line;
            pre_ordering.add(result);
        }
        Collections.sort(pre_ordering);
        // sorting finished...

        Iterator<String> iterator_after = pre_ordering.iterator();

        while (iterator_after.hasNext()) {
            String next = iterator_after.next();
            String[] split_after = next.split("\\?");
            hm.put(split_after[0], split_after);
        }
        bf.close();
    }
}