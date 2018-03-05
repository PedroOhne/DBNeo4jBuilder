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
class CanadaNode {

    private final String aer_key;
    private final String[] props; // one line
    private final CanadaReportDrugNode report_drugs;

    ArrayList<String> reactions;
    ArrayList<String> links;

    public CanadaNode(String key, String[] properties, CanadaReportDrugNode canadaRDnode) {
        this.aer_key = key;
        this.props = properties;
        this.report_drugs = canadaRDnode;
    }

    public String getAer_key() {
        return aer_key;
    }

    public String[] getProps() {
        return props;
    }

    public ArrayList<String> getReactions() {
        return reactions;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public CanadaReportDrugNode getReport_drugs() {
        return report_drugs;
    }

    public void setReactions(ArrayList<String> reactions) {
        this.reactions = reactions;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }

}
