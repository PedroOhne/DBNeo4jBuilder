/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import static de.master.neo4jdbbuilder.Properties.UNIQUE_SOURCE;
import static de.master.neo4jdbbuilder.Properties.unique_id_canada;
import java.util.ArrayList;
import java.util.HashMap;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
 class CanadaThread extends CanadaSuperInstanz implements Runnable {

        private final CanadaNode cn;

        public CanadaThread(CanadaNode c_n, HashMap<Integer,String[]> props, GraphDatabaseService db) {
            super(props ,db);
            this.cn = c_n;
        }

        @Override
        public void run() {
            Tools.startTime();
            try (Transaction tx = db_s.beginTx()) {
                Node demographicNODE = db_s.createNode(Entities.Patient);
                String[] props1 = cn.getProps();
                String[] props2 = Tools.returnCanadaProperyOneLineArray(props1[1]);

                for (int i = 0; i < props2.length; i++) {
                    demographicNODE.setProperty(getPatientProperties()[i], Tools.ConvertsString(props2[i]));
                }
                /*Unique_Source-Property*/
                demographicNODE.setProperty(UNIQUE_SOURCE, unique_id_canada);

                
                ArrayList<String> links = cn.getLinks();
                if (links instanceof ArrayList) {
                    for (String report_source : links) {
                        Node reportsourceNODE = db_s.createNode(Entities.Link);
                        String[] reportSPLIT = Tools.returnCanadaProperyOneLineArray(report_source);
                        for (int i = 0; i < reportSPLIT.length; i++) {
                            reportsourceNODE.setProperty(getLinkProperties()[i], Tools.ConvertsString(reportSPLIT[i]));
                        }
                        demographicNODE.createRelationshipTo(reportsourceNODE, EntitiesRelationships.PATIENT_LINK);
                    }
                }

                
                ArrayList<String> reactions = cn.getReactions();
                if (reactions instanceof ArrayList) {
                    for (String reaction : reactions) {
                        Node reactionNODE = db_s.createNode(Entities.Reaction);
                        String[] reactionSPLIT = Tools.returnCanadaProperyOneLineArray(reaction);
                        for (int i = 0; i < reactionSPLIT.length; i++) {
                            reactionNODE.setProperty(getReactionProperties()[i], Tools.ConvertsString(reactionSPLIT[i]));
                        }
                        demographicNODE.createRelationshipTo(reactionNODE, EntitiesRelationships.PATIENT_REACTION);
                    }
                }

                CanadaReportDrugNode report_drugs = cn.getReport_drugs();
                if (report_drugs instanceof CanadaReportDrugNode) {
                    ArrayList<String> indication_props = report_drugs.getIndication_props();
                    for (String drug : report_drugs.getReport_drug_props()) {
                        String[] drugSPLIT = Tools.returnCanadaProperyOneLineArray(drug);
                        Node drugNODE = db_s.createNode(Entities.Drug);
                        for (int i = 0; i < drugSPLIT.length; i++) {
                            drugNODE.setProperty(getReportDrugProperties()[i], Tools.ConvertsString(drugSPLIT[i]));
                        }
                        demographicNODE.createRelationshipTo(drugNODE, EntitiesRelationships.PATIENT_DRUG);
                        for (String indication_prop : indication_props) {
                            String[] indi_prop_values = Tools.returnCanadaProperyOneLineArray(indication_prop);
                            if (indi_prop_values[0].equals(drugSPLIT[0])) {
                                Node drugIndiNode = db_s.createNode(Entities.Indication);
                                for (int i = 0; i < indi_prop_values.length; i++) {
                                    if (i != 3) { //i==3 -> drugname property, already exists in drug.
                                        drugIndiNode.setProperty(getReportDrugIndicationProperties()[i], Tools.ConvertsString(indi_prop_values[i]));
                                    }
                                }
                                drugNODE.createRelationshipTo(drugIndiNode, EntitiesRelationships.DRUG_INDICATION);
                            }
                        }
                    }
                } else {
                    System.out.println("!?" + cn.getAer_key()); // tritt nicht mehr auf
                }
                tx.success();
            }
            Tools.printPassedTime(cn.getAer_key());
        }
    }