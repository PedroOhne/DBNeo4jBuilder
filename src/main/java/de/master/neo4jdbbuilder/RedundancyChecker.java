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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
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
    R_Worker rw;

    public RedundancyChecker(String databasee, String name_db) {

    }

    public RedundancyChecker(String databasee, String name_db, JTextArea area) throws IOException {
        this.database = databasee;
        this.database_name = name_db;
        this.j_area = area;
        R_Worker rww = new R_Worker(j_area, database, database_name);
        this.rw = rww;
        this.redundant_file = database + Tools.OSValidator() + Properties.config_redundancy_file;
    }

    public void executeWorker() {
        InitFileWriting();
        this.rw.execute();
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

    public class R_Worker extends SwingWorker<String, String> {

        JTextArea jarea;
        String database_path;
        String database_name;
        HashSet<Long> all_redy = new HashSet<>();
        HashSet<Long> all_uniq = new HashSet<>();

        public R_Worker(JTextArea a, String path, String name) throws IOException {
            this.jarea = a;
            this.database_name = name;
            this.database_path = path;
        }

        @Override
        protected String doInBackground() throws Exception {

            try (Transaction tx = db.beginTx();) {
                ResourceIterator<Node> findNodes = db.findNodes(Entities.Patient);
                while (findNodes.hasNext()) {
                    Node next = findNodes.next();
                    Object property = next.getProperty("primaryID");
                    String y = property.toString();
                    if (all_uniq.contains(y)) {
                        all_redy.add(next.getId());
                        publish(y);
                    } else {
                        all_uniq.add(next.getId());
                    }
                }
                tx.success();
            }
            CloseFileWriting();
            return null;
        }

        @Override
        protected void process(List<String> item) {
            //This updates the UI
            jarea.setText(item.get(item.size() - 1) + "\n");
        }
    }

}
