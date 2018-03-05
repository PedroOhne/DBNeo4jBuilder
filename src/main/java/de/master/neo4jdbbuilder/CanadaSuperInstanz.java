/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.util.HashMap;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author maestro
 */
public class CanadaSuperInstanz {

    /**
     * map contains all properties of the canadian data extract. 0 = patient,  1 =
     * drug_product, 2 = drug_product_ingredients, 3 = reactions, 4 = report_drug, 5
     * = report_drug_indication, 6 = links
     */
    private final HashMap<Integer, String[]> canadian_prop_map;
    GraphDatabaseService db_s;

    public CanadaSuperInstanz(HashMap<Integer, String[]> canadian_prop_map, GraphDatabaseService db_s) {
        this.canadian_prop_map = canadian_prop_map;
        this.db_s = db_s;
    }

    public String[] getPatientProperties() {
        return canadian_prop_map.get(0);
    }

    public String[] getDrugProductProperties() {
        return canadian_prop_map.get(1);
    }

    public String[] getDrugProdcutIngredientsProperties() {
        return canadian_prop_map.get(2);
    }

    public String[] getReactionProperties() {
        return canadian_prop_map.get(3);
    }

    public String[] getReportDrugProperties() {
        return canadian_prop_map.get(4);
    }

    public String[] getReportDrugIndicationProperties() {
        return canadian_prop_map.get(5);
    }

    public String[] getLinkProperties() {
        return canadian_prop_map.get(6);
    }
}
