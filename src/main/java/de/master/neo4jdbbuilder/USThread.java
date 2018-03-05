/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import static de.master.neo4jdbbuilder.Properties.UNIQUE_SOURCE;
import static de.master.neo4jdbbuilder.Properties.unique_id_usa;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 *
 */
class USThread implements Runnable {

    String regex = "([^\\x20]+)([\\x20]+)([\\/]{1}[\\d]*[\\/]{1})";
    Pattern pattern_regex = Pattern.compile(regex);
    Matcher m;
    GraphDatabaseService db_s;
    private final USgenerellNode b_n;

    /**
     * 0 = patient 1 = drugs 2 = reactions 3 = therapies 4 = indications 5 =
     * outcomes 6 = reportsources
     */
    private HashMap<Integer, String[]> all_props;

    public USThread(USgenerellNode b_n, HashMap<Integer, String[]> hm, GraphDatabaseService db) {
        this.b_n = b_n;
        this.all_props = hm;
        this.db_s = db;
    }

    @Override
    public void run() {
        try (Transaction tx = db_s.beginTx()) {
            String[] values_demographic = Tools.expandArray(b_n.getLine_to_split());
            Node demographicNODE = db_s.createNode(Entities.Patient);
            for (int i = 0; i < values_demographic.length; i++) {
                demographicNODE.setProperty(all_props.get(0)[i], values_demographic[i]);
            }
            demographicNODE.setProperty(UNIQUE_SOURCE, unique_id_usa);// additional instead of präfix/suffix

            ArrayList<String> report_sources = b_n.getReport_sources();
            for (String report_source : report_sources) {
                Node reportsourceNODE = db_s.createNode(Entities.ReportSource);
                String[] reportSPLIT = Tools.splitOneLine(report_source);
                for (int i = 0; i < reportSPLIT.length; i++) {
                    reportsourceNODE.setProperty(all_props.get(6)[i], reportSPLIT[i]);
                }
                reportsourceNODE.createRelationshipTo(demographicNODE, EntitiesRelationships.PATIENT_REPORT_SOURCES);
            }

            ArrayList<String> reactions = b_n.getReactions();
            for (String reaction : reactions) {
                Node reactionNODE = db_s.createNode(Entities.Reaction);
                String[] reactionSPLIT = Tools.splitOneLine(reaction);
                for (int i = 0; i < reactionSPLIT.length; i++) {
                    reactionNODE.setProperty(all_props.get(2)[i], reactionSPLIT[i]);
                }
                reactionNODE.createRelationshipTo(demographicNODE, EntitiesRelationships.PATIENT_REACTION);
            }

            ArrayList<String> outcomes = b_n.getOutcomes();
            for (String outcome : outcomes) {
                Node outcomeNODE = db_s.createNode(Entities.Outcome);
                String[] outcomeSPLIT = Tools.splitOneLine(outcome);
                for (int i = 0; i < outcomeSPLIT.length; i++) {
                    outcomeNODE.setProperty(all_props.get(5)[i], outcomeSPLIT[i]);
                }
                outcomeNODE.createRelationshipTo(demographicNODE, EntitiesRelationships.PATIENT_OUTCOME);
            }

            ArrayList<USadvancedNode> drugs = b_n.getDrugs();
            for (USadvancedNode drug : drugs) {
                String[] drugSPLIT = Tools.splitOneLine(drug.getLine_of_drug());
                Node drugNODE = db_s.createNode(Entities.Drug);
                for (int i = 0; i < drugSPLIT.length; i++) {
                    drugNODE.setProperty(all_props.get(1)[i], drugSPLIT[i]);
                }
                String newProperty = drugSPLIT[4].toUpperCase();
                m = pattern_regex.matcher(newProperty);
                if (m.find()) {
                    newProperty = m.group(1);
                }
                drugNODE.setProperty(all_props.get(1)[4], newProperty);

                ArrayList<String> indications = drug.getIndications();
                for (String indication : indications) {
                    Node indiNODE = db_s.createNode(Entities.Indication);
                    String[] indiSPLIT = Tools.splitOneLine(indication);
                    for (int i = 0; i < indiSPLIT.length; i++) {
                        indiNODE.setProperty(all_props.get(4)[i], indiSPLIT[i]);
                    }
                    indiNODE.createRelationshipTo(drugNODE, EntitiesRelationships.DRUG_INDICATION);
                }
                ArrayList<String> theras = drug.getTherapies();
                for (String ther : theras) {
                    Node therNODE = db_s.createNode(Entities.Therapy);
                    String[] indiTHERA = Tools.splitOneLine(ther);
                    for (int i = 0; i < indiTHERA.length; i++) {
                        therNODE.setProperty(all_props.get(3)[i], indiTHERA[i]);
                    }
                    therNODE.createRelationshipTo(drugNODE, EntitiesRelationships.DRUG_THERAPY);
                }
                drugNODE.createRelationshipTo(demographicNODE, EntitiesRelationships.PATIENT_DRUG);
            }
            tx.success();
        }
    }
}
