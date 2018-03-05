package de.master.neo4jdbbuilder;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
class USgenerellNode {

    private final String primary_id;
    private final String line_to_split;
    private final ArrayList<USadvancedNode> drugs;


    /*1..n*/
    private ArrayList<String> reactions = new ArrayList<>();
    /*0..n*/
    private ArrayList<String> outcomes = new ArrayList<>();
    private ArrayList<String> report_sources = new ArrayList<>();

    public USgenerellNode(String key, String line_to_split, ArrayList<USadvancedNode> drugs, String[] props) {
        this.primary_id = key;
        this.line_to_split = line_to_split;
        this.drugs = drugs;
    }



    public String[] getLine_to_split() {
        String[] split = Tools.splitOneLine(line_to_split);
        return split;
    }

    public void addReportSources(ArrayList<String> rs) {
        this.report_sources = rs;
    }

    public void addOutcomes(ArrayList<String> outcomes) {
        this.outcomes = outcomes;
    }

    public void addReaction(ArrayList<String> reaction) {
        this.reactions = reaction;
    }

    public String getPrimary_id() {
        return primary_id;
    }

    public ArrayList<String> getReactions() {
        return reactions;
    }

    public ArrayList<USadvancedNode> getDrugs() {
        return drugs;
    }

    public ArrayList<String> getOutcomes() {
        return outcomes;
    }

    public ArrayList<String> getReport_sources() {
        return report_sources;
    }

}
