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
    GraphDatabaseService db;

    public RedundancyChecker(String database, String name_db) {
        this.database = database;
        this.database_name = name_db;
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

        System.out.println("\n STRINGBUILDER");
        System.out.println(sb.toString());
        return props_redundant;
    }

    void removeDoubles(HashSet<Long> set_redundancies) {
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
                    for (Relationship relationship1 : relationships) {
                        relationship1.delete();
                        relationship1.getEndNode().delete(); // Indiaction
                    }
                    drugNode.delete();
                }

                // Outcome deleting
                Iterable<Relationship> relationships = next.getRelationships(EntitiesRelationships.HAS_PhO);
                for (Relationship relationship : relationships) {
                    relationship.delete();
                    relationship.getEndNode().delete();
                }

                // Reaction deleting
                Iterable<Relationship> relationships1 = next.getRelationships(EntitiesRelationships.PRESENTS_PpR);
                for (Relationship relationship : relationships1) {
                    relationship.delete();
                    relationship.getEndNode().delete();
                }

                // Report and ReportSources deleting
                Iterable<Relationship> relationships2 = next.getRelationships(EntitiesRelationships.DESCRIBES_RdP);
                for (Relationship relationship : relationships2) {
                    Node reportNODE = relationship.getStartNode();
                    Iterable<Relationship> relationships3 = reportNODE.getRelationships(EntitiesRelationships.PROVIDES_SpR);
                    for (Relationship relationship1 : relationships3) {
                        relationship1.delete();
                        relationship1.getStartNode().delete(); // Indication
                    }
                    relationship.delete();
                    reportNODE.delete();
                }
                next.delete();
                tx.success();
                Tools.printPassedTime(String.valueOf(set_redundancy));
            }
        }

        CloseFileWriting();

    }

}
