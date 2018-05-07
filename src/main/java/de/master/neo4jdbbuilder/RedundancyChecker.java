/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JTextArea;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author benfrenco
 */
public class RedundancyChecker {

    String database;
    String database_name;
    String redundant_file;
    JTextArea j_area;
    GraphDatabaseService db;

    public RedundancyChecker(String databasee, String name_db) {

    }

    public RedundancyChecker(String databasee, String name_db, JTextArea area) throws IOException {
        this.database = databasee;
        this.database_name = name_db;
        this.j_area = area;
        this.redundant_file = database + Tools.OSValidator() + Properties.config_redundancy_file;
    }

    public HashSet<Long> readFileRed() throws FileNotFoundException {

        InitFileWriting();
        HashSet<Long> removable_ids = new HashSet<>();

        BufferedReader br = new BufferedReader(new FileReader(new File(redundant_file)));
        Iterator<String> iterator = br.lines().iterator();
        try (Transaction tx = db.beginTx()) {
            while (iterator.hasNext()) {
                String next = iterator.next();
                Long l = Long.valueOf(next);
                l = l + 0;
                removable_ids.add(l);
            }
            tx.success();
        }

        CloseFileWriting();
        return removable_ids;
    }

    public void InitFileWriting() {
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        db = dbFactory.newEmbeddedDatabase(new File(database + Tools.OSValidator() + database_name));
    }

    public void CloseFileWriting() {
        db.shutdown();
    }

    public HashSet<Long> checkDoubles() throws IOException {
        String symb = Tools.OSValidator();
        HashSet<String> props_unique = new HashSet<>();
        HashSet<Long> props_redundant = new HashSet<>();
        try (Transaction tx = db.beginTx();) {
            ResourceIterator<Node> findNodes = db.findNodes(Entities.Patient);
            while (findNodes.hasNext()) {
                Node next = findNodes.next();
                Object property = next.getProperty("primaryID");
                String y = property.toString();
                if (props_unique.contains(y)) {
                    props_redundant.add(next.getId());
                } else {
                    props_unique.add(y);
                }
            }
            tx.success();
        }

        System.out.println("UNIQUE: " + props_unique.size());
        System.out.println("REDUNDANT: " + props_redundant.size());
        redundant_file = database + symb + Properties.config_redundancy_file;
        File f = new File(redundant_file);
        f.createNewFile();
        FileWriter fw = new FileWriter(f);
        StringBuilder sb = new StringBuilder();
        for (Long long1 : props_redundant) {
            String value = String.valueOf(long1);
            sb.append(value + "\n");
            System.out.println(long1);
            fw.write(value + "\n");
            fw.flush();
        }
        fw.close();

        return props_redundant;
    }

    void removeDoubles(HashSet<Long> set_redundancies) {
        HashSet<Long> drugs_relations = new HashSet<>();
        HashSet<Long> drugs_nodes = new HashSet<>();
        InitFileWriting();
        for (Long set_redundancy : set_redundancies) {
            try (Transaction tx = db.beginTx()) {
                Tools.startTime();
                Node next = db.getNodeById(set_redundancy);

                // DRUG and Indiaction deleting...
                Iterable<Relationship> relationships_drug = next.getRelationships(EntitiesRelationships.TAKES_PtD);
                for (Relationship relationship : relationships_drug) {
                    Node drugNode = relationship.getEndNode(); // DRUG 
                    Iterable<Relationship> relationships = drugNode.getRelationships(EntitiesRelationships.HAS_DhI); // Drug Indication
                    if (relationships != null) {
                        for (Relationship relationship1 : relationships) {
                            drugs_relations.add(relationship1.getId());
                            drugs_nodes.add(relationship1.getEndNode().getId());
                        }
                    }
                    drugs_relations.add(relationship.getId());
                    drugs_nodes.add(drugNode.getId());
                }

                // Outcome deleting
                Iterable<Relationship> relationships = next.getRelationships(EntitiesRelationships.HAS_PhO);
                if (relationships != null) {
                    for (Relationship relationship : relationships) {
                        drugs_relations.add(relationship.getId());
                        drugs_nodes.add(relationship.getEndNode().getId());
                    }
                }

                // Reaction deleting
                Iterable<Relationship> relationships1 = next.getRelationships(EntitiesRelationships.PRESENTS_PpR);
                if (relationships1 != null) {
                    for (Relationship relationship : relationships1) {
                        drugs_relations.add(relationship.getId());
                        drugs_nodes.add(relationship.getEndNode().getId());
                    }
                }

                // Report and ReportSources deleting
                Iterable<Relationship> relationships2 = next.getRelationships(EntitiesRelationships.DESCRIBES_RdP);
                if (relationships2 != null) {
                    for (Relationship relationship : relationships2) {
                        Node reportNODE = relationship.getStartNode();
                        Iterable<Relationship> relationships3 = reportNODE.getRelationships(EntitiesRelationships.PROVIDES_SpR);
                        if (relationships3 != null) {
                            for (Relationship relationship1 : relationships3) {
                                drugs_relations.add(relationship1.getId());
                                drugs_nodes.add(relationship1.getStartNode().getId());
                            }
                            drugs_relations.add(relationship.getId());
                            drugs_nodes.add(reportNODE.getId());
                        }
                    }
                }
                drugs_nodes.add(next.getId());
                tx.success();
                Tools.printPassedTime(String.valueOf(set_redundancy));
            }
        }

        System.out.println("delete relations..");
        try (Transaction tx = db.beginTx()) {
            drugs_relations.forEach((drugs_relation) -> {
                db.getRelationshipById(drugs_relation).delete();
            });
            tx.success();
        }

        System.out.println("delete nodes..");
        try (Transaction tx = db.beginTx()) {
            drugs_nodes.forEach((drugs_) -> {
                db.getNodeById(drugs_).delete();
            });
            tx.success();
        }

        CloseFileWriting();

    }

    String checkFollowUpRecords() throws IOException {

        Object uniq = "USA";
        int big_size = 0;
        ConcurrentHashMap<Object, HashMap<Integer, Long>> follow_up_reports = new ConcurrentHashMap<>();

        try (Transaction tx = db.beginTx()) {
            ResourceIterator<Node> findNodes = db.findNodes(Entities.Report, "unique_source", "USA");
            Iterator<Node> iterator = findNodes.stream().iterator();
            while (iterator.hasNext()) {
                Node report = iterator.next();
                Long id_intern = report.getId();
                Object property = report.getProperty(Properties.UNIQUE_SOURCE);
                Object case_id_ = report.getProperty("caseID");
                String caseV = report.getProperty("caseversion").toString();
                if (property.equals(uniq)) {
                    if (!follow_up_reports.containsKey(case_id_)) {
                        HashMap<Integer, Long> primes = new HashMap<>();
                        int parseInt = Integer.parseInt(caseV);
                        primes.put(parseInt, id_intern);
                        follow_up_reports.put(case_id_, primes);
                    } else {
                        HashMap<Integer, Long> get = follow_up_reports.get(case_id_);
                        int parseInt = Integer.parseInt(caseV);
                        get.put(parseInt, id_intern);
                        follow_up_reports.put(case_id_, get);
                    }
                }
                big_size++;
            }
            tx.success();

            System.out.println("before: " + big_size);
            
            for (Map.Entry<Object, HashMap<Integer, Long>> entry : follow_up_reports.entrySet()) {
                HashMap<Integer, Long> value_all = entry.getValue();
                int size = value_all.size();

                if (size > 1) {
                    int max = 0;
                    for (Map.Entry<Integer, Long> entry1 : value_all.entrySet()) {
                        if (entry1.getKey() >= max) {
                            max = entry1.getKey();
                        }
                    }
                    entry.getValue().remove(max);
                    for (Map.Entry<Integer, Long> entry1 : value_all.entrySet()) {
                        deleteNodeAndSubgraph(db, entry1.getValue());
                    }
                } else {
                    follow_up_reports.remove(entry.getKey());
                }
            }
        }
        return "" + follow_up_reports.size();
    }

    public void deleteFUreports(HashSet<HashMap<Integer, Long>> follow_up_reports) {

        for (HashMap<Integer, Long> entry : follow_up_reports) {
            for (Long value : entry.values()) {
                deleteNodeAndSubgraph(db, value);
            }
        }

    }

    public void deleteNodeAndSubgraph(GraphDatabaseService db, Long id) {

        HashSet<Relationship> all_rels = new HashSet<>();
        HashSet<Node> all_nodes = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            Node report_node = db.getNodeById(id);
            Iterator<Relationship> Provides_SpR = report_node.getRelationships(EntitiesRelationships.PROVIDES_SpR).iterator();
            all_nodes.add(report_node);

            while (Provides_SpR.hasNext()) {
                Relationship next = Provides_SpR.next();
                if (next instanceof Relationship) {
                    all_rels.add(next);
                    if (next.getStartNode() instanceof Node) {
                        Node sourceNode = next.getStartNode();
                        all_nodes.add(sourceNode);
                    }
                }
            }

            Relationship DESCRIBES_RdP = report_node.getSingleRelationship(EntitiesRelationships.DESCRIBES_RdP, Direction.OUTGOING);
            if (DESCRIBES_RdP instanceof Relationship) {
                all_rels.add(DESCRIBES_RdP);
                Node patientNODE = DESCRIBES_RdP.getEndNode();
                if (patientNODE instanceof Node) {
                    Iterator<Relationship> TAKES_PtD = patientNODE.getRelationships(EntitiesRelationships.TAKES_PtD).iterator();
                    while (TAKES_PtD.hasNext()) {
                        Relationship TAKES_PTD = TAKES_PtD.next();
                        Node drugNODE = TAKES_PTD.getEndNode();
                        Iterator<Relationship> iterator = drugNODE.getRelationships(Direction.OUTGOING).iterator();
                        while (iterator.hasNext()) {
                            Relationship next = iterator.next();
                            if (next instanceof Relationship) {
                                Node indiNODE = next.getEndNode();
                                all_nodes.add(indiNODE);
                                all_rels.add(next);
                            }
                        }

                        all_rels.add(TAKES_PTD);
                        all_nodes.add(drugNODE);
                    }

                    Relationship HAS_PhO = patientNODE.getSingleRelationship(EntitiesRelationships.HAS_PhO, Direction.OUTGOING);
                    if (HAS_PhO instanceof Relationship) {
                        Node outcNODE = HAS_PhO.getEndNode();
                        all_nodes.add(outcNODE);
                        all_rels.add(HAS_PhO);
                    }

                    Iterator<Relationship> PRESENTS_PpR = patientNODE.getRelationships(EntitiesRelationships.PRESENTS_PpR).iterator();
                    while (PRESENTS_PpR.hasNext()) {
                        Relationship PRESENTS_PPR = PRESENTS_PpR.next();
                        Node reacNODE = PRESENTS_PPR.getEndNode();
                        all_nodes.add(reacNODE);
                        all_rels.add(PRESENTS_PPR);
                    }
                }
            }

            all_rels.stream().filter((all_rel) -> (all_rel instanceof Relationship)).forEachOrdered((all_rel) -> {
                all_rel.delete();
            });

            all_nodes.stream().filter((all_node) -> (all_node instanceof Node)).forEachOrdered((all_node) -> {
                all_node.delete();
            });

            tx.success();
        }
    }

}
