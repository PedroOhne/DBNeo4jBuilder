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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
class CanadaThread_F implements Runnable {

    private final CanadaNode cn;
    private final GraphDatabaseService gdb;
    private final HashMap<String, HashMap<String, Integer>> configs;
    String regex = "(.+)(\\.|\\,|[\\/].*[\\/])$";
    Pattern pattern_regex = Pattern.compile(regex);
    Matcher m;
    ConcurrentHashMap<String, String> mp_db_id;

    public CanadaThread_F(CanadaNode c_n, HashMap<String, HashMap<String, Integer>> props, GraphDatabaseService db,
            ConcurrentHashMap<String, String> mp) {
        this.cn = c_n;
        this.gdb = db;
        this.configs = props;
        this.mp_db_id = mp;
    }

    @Override
    public void run() {
        Tools.startTime();
        try (Transaction tx = gdb.beginTx()) {

            HashMap<String, Integer> all_report_attributs = configs.get("Report");
            HashMap<String, Integer> all_patient_attributs = configs.get("Patient");
            HashMap<String, Integer> all_source_attributs = configs.get("Source");
            HashMap<String, Integer> all_outcome_attributs = configs.get("Outcome");
            HashMap<String, Integer> all_reaction_attributs = configs.get("Reaction");
            HashMap<String, Integer> all_drug_attributs = configs.get("Drug");
            HashMap<String, Integer> all_indication_attributs = configs.get("Indication");
            HashMap<String, Integer> all_therapy_attributs = configs.get("Therapy");

            Node reportNODE = gdb.createNode(Entities.Report);
            Node patientNODE = gdb.createNode(Entities.Patient);
            Node sourceNODE = gdb.createNode(Entities.Source);

            String[] props1 = cn.getProps();
            String[] props2 = Tools.returnCanadaProperyOneLineArray(props1[1]);
            for (int i = 0; i < props2.length; i++) {
                props2[i] = props2[i].replaceAll("\"", "");
            }

            /*Report-Node-Setting-Properties*/
            for (Map.Entry<String, Integer> entry : all_report_attributs.entrySet()) {
                String value_to_set = "";
                if (entry.getValue() instanceof Integer) {
                    String value = props2[entry.getValue()];
                    value_to_set = value;
                    switch (entry.getValue()) {
                        case 0:
                            value_to_set = Properties.präfix_can + props2[entry.getValue()];
                            break;
                        case 3:
                            value_to_set = Tools.convertDateToStandard(props2[entry.getValue()]);
                            break;
                        case 4:
                            value_to_set = Tools.convertDateToStandard(props2[entry.getValue()]);
                            break;
                        case 34:
                            value_to_set = Tools.convertReporterTypeToStandard(props2[entry.getValue()]);
                            break;
                    }
                } else {
                    value_to_set = "n/a";
                }
                reportNODE.setProperty(entry.getKey(), value_to_set);
            }
            reportNODE.setProperty(UNIQUE_SOURCE, unique_id_canada);

            /*Patient-Node-Setting-Properties*/
            for (Map.Entry<String, Integer> entry : all_patient_attributs.entrySet()) {
                String value_to_set = "";
                if (entry.getValue() instanceof Integer) {
                    String value = props2[entry.getValue()];
                    value_to_set = value;
                    switch (entry.getValue()) {
                        case 20:
                            value_to_set = Tools.convertWeightUnitToStandard(value);
                            break;
                        case 0:
                            value_to_set = Properties.präfix_can + value;
                            break;
                        case 14:
                            value_to_set = Tools.convertAgeUnitToStandard(value);
                            break;
                        case 13:
                            value_to_set = Tools.convertAgeYearsToAgeGroup(value);
                            break;
                        case 10:
                            value_to_set = Tools.convertGenderToStandard(value);
                            break;
                        case 23:
                            value_to_set = Tools.convertHeightUnitToStandard(value);
                            break;
                    }
                } else {
                    value_to_set = "n/a";
                }
                patientNODE.setProperty(entry.getKey(), value_to_set);
            }
            reportNODE.createRelationshipTo(patientNODE, EntitiesRelationships.DESCRIBES_RdP);

            /*Source-Node-Setting-Properties*/
            for (Map.Entry<String, Integer> entry : all_source_attributs.entrySet()) {
                String value_to_set = "";
                if (entry.getValue() instanceof Integer) {
                    String value = props2[entry.getValue()];
                    value_to_set = value;
                    switch (entry.getValue()) {
                        case 6:
                            value_to_set = Tools.convertSourceKanadaToStandard(value);
                            break;
                    }
                } else {
                    value_to_set = "n/a";
                }
                sourceNODE.setProperty(entry.getKey(), value_to_set);
            }
            sourceNODE.createRelationshipTo(reportNODE, EntitiesRelationships.PROVIDES_SpR);

            /*Outcome-Node-Setting-Properties*/
            boolean settter = false;
            Node outcomeNode = gdb.createNode(Entities.Outcome);

            for (Map.Entry<String, Integer> entry : all_outcome_attributs.entrySet()) {
                String value_to_set = "";
                if (entry.getValue() instanceof Integer) {
                    String value = props2[entry.getValue()];
                    value_to_set = value;
                    switch (entry.getValue()) {
                        case 25:
                            value_to_set = Tools.convertSeriousnessCodeToStandard(value);
                            break;
                        case 16:
                            value_to_set = Tools.convertOutcomeCodeToStandard(value);
                            if (!value_to_set.equals("")) {
                                settter = true;
                            }
                            break;
                    }
                } else {
                    value_to_set = "n/a";
                }
                outcomeNode.setProperty(entry.getKey(), value_to_set);
            }

            if (settter == false) {
                outcomeNode.delete();
            } else {
                patientNODE.createRelationshipTo(outcomeNode, EntitiesRelationships.HAS_PhO);
            }

            /*Reaction-Node-Setting-Properties*/
            ArrayList<String> reactions = cn.getReactions();
            if (reactions instanceof ArrayList) {
                for (String reaction : reactions) {
                    Node reactionNODE = gdb.createNode(Entities.Reaction);
                    String[] reactionSPLIT = Tools.returnCanadaProperyOneLineArray(reaction);

                    for (int i = 0; i < reactionSPLIT.length; i++) {
                        reactionSPLIT[i] = reactionSPLIT[i].replaceAll("\"", "");
                    }

                    for (Map.Entry<String, Integer> entry : all_reaction_attributs.entrySet()) {
                        String value_to_set = "";
                        if (entry.getValue() instanceof Integer) {
                            String value = reactionSPLIT[entry.getValue()];
                            value_to_set = value;
                            switch (entry.getValue()) {
                                case 3:
                                    value_to_set = Tools.convertReactionDurationCodetoStandard(value);
                                    break;
                            }
                        } else {
                            value_to_set = "n/a";
                        }
                        reactionNODE.setProperty(entry.getKey(), value_to_set);
                    }
                    patientNODE.createRelationshipTo(reactionNODE, EntitiesRelationships.PRESENTS_PpR);
                }
            }

            /*Drug-Node-Setting-Properties*/
            CanadaReportDrugNode report_drugs = cn.getReport_drugs();
            if (report_drugs instanceof CanadaReportDrugNode) {
                ArrayList<String> indication_props = report_drugs.getIndication_props();
                for (String drug : report_drugs.getReport_drug_props()) {
                    String[] drugSPLIT = Tools.returnCanadaProperyOneLineArray(drug);
                    for (int i = 0; i < drugSPLIT.length; i++) {
                        String string = drugSPLIT[i];
                    }

                    Node drugNODE = gdb.createNode(Entities.Drug);

                    for (int i = 0; i < drugSPLIT.length; i++) {
                        drugSPLIT[i] = drugSPLIT[i].replaceAll("\"", "");
                    }

                    String value_to_set = "";
                    String db_id = "";

                    for (Map.Entry<String, Integer> entry : all_drug_attributs.entrySet()) {
                        if (entry.getValue() instanceof Integer) {
                            String value = drugSPLIT[entry.getValue()];
                            value_to_set = value;
                            switch (entry.getValue()) {
                                case 9:
                                    value_to_set = Tools.convertDosageUnitToStandard(value);
                                    break;
                                case 15:
                                    value_to_set = Tools.convertFreqUnitToStandard(value);
                                    break;
                                case 3:
                                    if (mp_db_id.containsKey(value_to_set.toUpperCase())) {
                                        db_id = mp_db_id.get(value_to_set);
                                        if (db_id != null) {
                                            drugNODE.setProperty(Properties.UNQIUE_DRUGBANK_ID, db_id);
                                        }
                                    } else {
                                        drugNODE.setProperty(Properties.UNQIUE_DRUGBANK_ID, "");
                                    }
                                    /**
                                     * fix of weird drugnames. Like
                                     * "MULTIVITAMIN /00097801/"
                                     */
                                    m = pattern_regex.matcher(value.toUpperCase());
                                    if (m.find()) {
                                        value_to_set = m.group(1).toUpperCase();
                                    }
                                    /**
                                     * fix ends.
                                     */
                                    break;
                            }
                        } else {
                            value_to_set = "n/a";
                        }
                        drugNODE.setProperty(entry.getKey(), value_to_set);
                    }

                    Relationship PtD = patientNODE.createRelationshipTo(drugNODE, EntitiesRelationships.TAKES_PtD);

                    for (Map.Entry<String, Integer> entry : all_therapy_attributs.entrySet()) {
                        value_to_set = "";
                        if (entry.getValue() instanceof Integer) {
                            switch (entry.getValue()) {
                                case 0:
                                    value_to_set = drugSPLIT[0];
                                    break;
                                case 17:
                                    value_to_set = drugSPLIT[17];
                                    break;
                                case 18:
                                    value_to_set = Tools.convertTherapyDurationCodetoStandard(drugSPLIT[18]);
                                    break;
                            }
                        } else {
                            value_to_set = "n/a";
                        }
                        PtD.setProperty(entry.getKey(), value_to_set);
                    }

                    for (String indication_prop : indication_props) {
                        String[] indi_prop_values = Tools.returnCanadaProperyOneLineArray(indication_prop);
                        indi_prop_values[0] = indi_prop_values[0].replaceAll("\"", "");

                        if (indi_prop_values[0].equals(drugSPLIT[0])) { // Zuordnung!

                            for (int i = 0; i < indi_prop_values.length; i++) {
                                indi_prop_values[i] = indi_prop_values[i].replaceAll("\"", "");
                            }

                            /*Indication-Node-Setting-Properties*/
                            String d_seq = "";
                            Node drugIndiNode = gdb.createNode(Entities.Indication);
                            for (Map.Entry<String, Integer> entry : all_indication_attributs.entrySet()) {
                                value_to_set = "";
                                if (entry.getValue() instanceof Integer) {
                                    String value = indi_prop_values[entry.getValue()];
                                    value_to_set = value;
                                } else {
                                    value_to_set = "n/a";
                                }
                                drugIndiNode.setProperty(entry.getKey(), value_to_set);
                            }
                            drugNODE.createRelationshipTo(drugIndiNode, EntitiesRelationships.HAS_DhI);
                        }
                    }
                }
            } else {
                System.out.println("!?" + cn.getAer_key()); // tritt nicht mehr auf
            }
            tx.success();
        }
    }
}
