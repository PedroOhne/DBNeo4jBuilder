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
class CanadaExtendedNode {

    private final String drug_pro_id;
    private final String[] drug_pro_props;
    ArrayList<String> props_ingridients = new ArrayList<>();

    public CanadaExtendedNode(String key, String[] properties) {
        this.drug_pro_id = key;
        this.drug_pro_props = properties;
    }

    public ArrayList<String> getProps_ingridients() {
        return props_ingridients;
    }

    public String[] getProperty() {
        return drug_pro_props;
    }

    public void setDrugProductIngridients(ArrayList<String> al) {
        this.props_ingridients = al;
    }

    public String getDrug_pro_id() {
        return drug_pro_id;
    }

}
