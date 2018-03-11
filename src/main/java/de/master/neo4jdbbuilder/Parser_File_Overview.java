/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author benfrenco
 */
public class Parser_File_Overview {

    private final String name;
    private ArrayList<Parser_File_Entry> p_infos;

    public Parser_File_Overview(String name, ArrayList<Parser_File_Entry> p_infos) {
        this.name = name;
        this.p_infos = p_infos;
    }

    Double convertForShowOnList() {
        Double dd = 0.0;
        for (Parser_File_Entry pfe : p_infos) {
            if (pfe.getName().startsWith("YCS")) {
                return 200.0;
            } else if (pfe.getName().equals("CANADA Parser")) {
                try {
                    System.out.println(pfe.getUrl());
                    double checkFileSizeOfCanadaZipFile = Tools.checkFileSizeOfCanadaZipFile(pfe.getUrl());
                    return checkFileSizeOfCanadaZipFile;
                } catch (IOException ex) {
                    Logger.getLogger(Parser_File_Overview.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String double_unformatted = pfe.getSize().replaceAll("MB", "");
                if (!pfe.getUrl().startsWith("https")) {
                    pfe.setUrl("https://www.fda.gov" + pfe.getUrl());
                }
                if (double_unformatted.equals("")) {
                } else {
                    Double d = Double.parseDouble(double_unformatted);
                    dd = dd + d;
                    dd = Tools.roundDouble(dd, 2);
                }
            } 
        }
        return dd;
    }

    public ArrayList<Parser_File_Entry> getP_infos() {
        return p_infos;
    }

    @Override
    public String toString() {
        return this.name + "\t" + convertForShowOnList() + " MB"; //To change body of generated methods, choose Tools | Templates.
    }

}
