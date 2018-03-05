/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alexander Maier <amaier at cebitec.uni-bielefeld.de>
 */
class USadvancedNode {


    private final String primary_id;
    private final String secondary_id;
    private final String line_of_drug;
    private ArrayList<String> therapies = new ArrayList<>();
    private ArrayList<String> indications = new ArrayList<>();

    
    

    public USadvancedNode(String primary_id, String second_id, String line) {
        this.line_of_drug = line;
        this.primary_id = primary_id;
        this.secondary_id = second_id;
    }

    public void setTherapies(ArrayList<String> therapies) {
        this.therapies = therapies;
    }

    public void setIndications(ArrayList<String> indications) {
        this.indications = indications;
    }

    public String getPrimary_id() {
        return primary_id;
    }

    public String getSecondary_id() {
        return secondary_id;
    }

    public ArrayList<String> getTherapies() {
        return therapies;
    }

    public ArrayList<String> getIndications() {
        return indications;
    }

    public String getLine_of_drug() {
        return line_of_drug;
    }

}
