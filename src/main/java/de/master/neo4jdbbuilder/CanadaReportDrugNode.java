/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.util.ArrayList;

/**
 *
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */

class CanadaReportDrugNode {

    private final String report_drug_id;
    private final ArrayList<String> report_drug_props;
    private final ArrayList<String> indication_props;

    public CanadaReportDrugNode(String key, ArrayList<String> properties, ArrayList<String> indi_properties) {
        this.report_drug_props = properties;
        this.report_drug_id = key;
        if (indi_properties instanceof ArrayList && indi_properties.size() > 0) {
            this.indication_props = indi_properties;
        } else {
            this.indication_props = new ArrayList<>();
        }
    }

    public String getReport_drug_id() {
        return report_drug_id;
    }

    public ArrayList<String> getReport_drug_props() {
        return report_drug_props;
    }

    public ArrayList<String> getIndication_props() {
        return indication_props;
    }

}