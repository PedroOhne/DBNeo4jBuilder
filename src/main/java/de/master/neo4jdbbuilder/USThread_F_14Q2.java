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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 *
 */
class USThread_F_14Q2 implements Runnable {

    GraphDatabaseService db_s;
    private final USgenerellNode b_n;
    String regex = "(.+)(\\.|\\,|[\\/].*[\\/])$";
    Pattern pattern_regex = Pattern.compile(regex);
    Matcher m;
    ConcurrentHashMap<String, String> mp_db_id;

    private HashMap<String, HashMap<String, Integer>> generall_properties;

    public USThread_F_14Q2(USgenerellNode b_n, HashMap<String, HashMap<String, Integer>> hm, GraphDatabaseService db,
            ConcurrentHashMap<String, String> mp) {
        this.b_n = b_n;
        this.generall_properties = hm;
        this.db_s = db;
        this.mp_db_id = mp;
    }

    @Override
    public void run() {
        try (Transaction tx = db_s.beginTx()) {

            String[] values_demographic = b_n.getLine_to_split();
            HashMap<String, Integer> patient_props = generall_properties.get("Patient");
            HashMap<String, Integer> report_props = generall_properties.get("Report");
            HashMap<String, Integer> reaction_props = generall_properties.get("Reaction");
            HashMap<String, Integer> reportsource_props = generall_properties.get("RPSR");
            HashMap<String, Integer> drug_props = generall_properties.get("Drug");
            HashMap<String, Integer> indication_props = generall_properties.get("Indication");
            HashMap<String, Integer> therapy_props = generall_properties.get("Therapy");

            String prop_reaction_event_date = values_demographic[4];
            report_props.remove("event_dt");

            Node patientNODE = db_s.createNode(Entities.Patient);

            for (Map.Entry<String, Integer> entry : patient_props.entrySet()) {
                if (entry.getValue() == -1) {
                    patientNODE.setProperty(entry.getKey(), Properties.information_not_available);
                } else if (entry.getValue() == 0) {
                    patientNODE.setProperty(entry.getKey(), Properties.präfix_usa + values_demographic[entry.getValue()]);
                } else {
                    patientNODE.setProperty(entry.getKey(), values_demographic[entry.getValue()]);
                }
            }

            Node reportNODE = db_s.createNode(Entities.Report);
            for (Map.Entry<String, Integer> entry : report_props.entrySet()) {
                if (entry.getValue() == -1) {
                    reportNODE.setProperty(entry.getKey(), Properties.information_not_available);
                } else if (entry.getValue() == 0) {
                    reportNODE.setProperty(entry.getKey(), Properties.präfix_usa + values_demographic[entry.getValue()]);
                } else {
                    reportNODE.setProperty(entry.getKey(), values_demographic[entry.getValue()]);
                }
            }
            reportNODE.setProperty(UNIQUE_SOURCE, unique_id_usa);
            reportNODE.removeProperty("event_dt");
            reportNODE.createRelationshipTo(patientNODE, EntitiesRelationships.DESCRIBES_RdP);

            ArrayList<String> report_sources = b_n.getReport_sources();
            for (String report_source : report_sources) {
                Node reportsourceNODE = db_s.createNode(Entities.Source);
                String[] reportSPLIT = Tools.splitOneLine(report_source);
                for (Map.Entry<String, Integer> entry : reportsource_props.entrySet()) {
                    if (entry.getValue() == -1) {
                        reportsourceNODE.setProperty(entry.getKey(), Properties.information_not_available);
                    } else {
                        reportsourceNODE.setProperty(entry.getKey(), reportSPLIT[entry.getValue()]);
                    }
                }
                reportsourceNODE.createRelationshipTo(reportNODE, EntitiesRelationships.PROVIDES_SpR);
            }

            ArrayList<String> reactions = b_n.getReactions();
            for (String reaction : reactions) {
                Node reactionNODE = db_s.createNode(Entities.Reaction);
                String[] reactionSPLIT = Tools.splitOneLine(reaction);
                if (reactionSPLIT.length == 3) {
                    reactionSPLIT = Tools.expandArray(reactionSPLIT);
                }
                for (Map.Entry<String, Integer> entry : reaction_props.entrySet()) {
                    if (entry.getValue() == -1) {
                        reactionNODE.setProperty(entry.getKey(), Properties.information_not_available);
                    } else {
                        reactionNODE.setProperty(entry.getKey(), reactionSPLIT[entry.getValue()]);
                    }
                }
                reactionNODE.setProperty(Properties.fusion_usa_date_adr, prop_reaction_event_date);
                patientNODE.createRelationshipTo(reactionNODE, EntitiesRelationships.PRESENTS_PpR);
            }

            ArrayList<String> outcomes = b_n.getOutcomes();
            Node outcomeNODE = null;
            if (outcomes.size() > 0) {
                outcomeNODE = db_s.createNode(Entities.Outcome);
                String de = "2";
                String ds = "2";
                String hr = "2";
                String ot = "2";
                String lt = "2";
                String ca = "2";
                for (String outcome : outcomes) {
                    String[] outcomeSPLIT = Tools.splitOneLine(outcome);
                    String code = outcomeSPLIT[2];
                    switch (code) {
                        case "DE":
                            de = "1";
                            break;
                        case "LT":
                            lt = "1";
                            break;
                        case "HO":
                            hr = "1";
                            break;
                        case "DS":
                            ds = "1";
                            break;
                        case "CA":
                            ca = "1";
                            break;
                        case "OT":
                            ot = "1";
                            break;
                    }
                }

                outcomeNODE.setProperty(Properties.fusion_canada_seriousness, "n/a");
                outcomeNODE.setProperty(Properties.fusion_o_death, de);
                outcomeNODE.setProperty(Properties.fusion_o_dis, ds);
                outcomeNODE.setProperty(Properties.fusion_o_congential_abn, ca);
                outcomeNODE.setProperty(Properties.fusion_o_life_t, lt);
                outcomeNODE.setProperty(Properties.fusion_o_hosp_req, hr);
                outcomeNODE.setProperty(Properties.fusion_o_ot_med_cond, ot);
                patientNODE.createRelationshipTo(outcomeNODE, EntitiesRelationships.HAS_PhO);
            } else {
            }

            String db_id = "";
            ArrayList<USadvancedNode> drugs = b_n.getDrugs();
            for (USadvancedNode drug : drugs) {

                String[] drugSPLIT = Tools.splitOneLine(drug.getLine_of_drug());
                Node drugNODE = db_s.createNode(Entities.Drug);
                for (Map.Entry<String, Integer> entry : drug_props.entrySet()) {
                    if (entry.getValue() == -1) {
                        drugNODE.setProperty(entry.getKey(), Properties.information_not_available);
                    } else {
                        drugNODE.setProperty(entry.getKey(), drugSPLIT[entry.getValue()]);
                    }
                }

                String newProperty = drugSPLIT[4].toUpperCase();
                m = pattern_regex.matcher(newProperty);
                if (m.find()) {
                    newProperty = m.group(1).toUpperCase();
                    drugNODE.removeProperty("drugname");
                    drugNODE.setProperty("drugname", newProperty);
                }

                if (mp_db_id.containsKey(newProperty)) {
                    db_id = mp_db_id.get(newProperty);
                    if (db_id != null) {
                        drugNODE.setProperty(Properties.UNQIUE_DRUGBANK_ID, db_id);
                    }
                } else {
                    drugNODE.setProperty(Properties.UNQIUE_DRUGBANK_ID, "");
                }

                String convertFreqUSAtoStandard = Tools.convertFreqUSAtoStandard(drugSPLIT[drugSPLIT.length - 1]);
                drugNODE.removeProperty(Properties.fusion_drug_freq);
                drugNODE.setProperty(Properties.fusion_drug_freq, // Convert Freq.
                        convertFreqUSAtoStandard);
                Relationship TAKES_PtD = patientNODE.createRelationshipTo(drugNODE, EntitiesRelationships.TAKES_PtD);

                ArrayList<String> indications = drug.getIndications();
                for (String indication : indications) {
                    Node indiNODE = db_s.createNode(Entities.Indication);
                    String[] indiSPLIT = Tools.splitOneLine(indication);
                    for (Map.Entry<String, Integer> entry : indication_props.entrySet()) {
                        if (entry.getValue() == -1) {
                            indiNODE.setProperty(entry.getKey(), Properties.information_not_available);
                        } else {
                            indiNODE.setProperty(entry.getKey(), indiSPLIT[entry.getValue()]);
                        }
                    }
                    drugNODE.createRelationshipTo(indiNODE, EntitiesRelationships.HAS_DhI);
                }

                ArrayList<String> theras = drug.getTherapies();
                int c = 0;
                for (String ther : theras) {
                    if (c > 0) {
                        Relationship TAKES_PtD_2 = patientNODE.createRelationshipTo(drugNODE, EntitiesRelationships.TAKES_PtD);
                        String[] indiTHERA = Tools.splitOneLine(ther);
                        for (Map.Entry<String, Integer> entry : therapy_props.entrySet()) {
                            if (entry.getValue() == -1) {
                                TAKES_PtD_2.setProperty(entry.getKey(), Properties.information_not_available);
                            } else {
                                TAKES_PtD_2.setProperty(entry.getKey(), indiTHERA[entry.getValue()]);
                            }
                        }

                    } else {
                        String[] indiTHERA = Tools.splitOneLine(ther);
                        for (Map.Entry<String, Integer> entry : therapy_props.entrySet()) {
                            if (entry.getValue() == -1) {
                                TAKES_PtD.setProperty(entry.getKey(), Properties.information_not_available);
                            } else {
                                TAKES_PtD.setProperty(entry.getKey(), indiTHERA[entry.getValue()]);
                            }
                        }
                        c++;
                    }
                }

            }
            tx.success();
        }
    }
}
