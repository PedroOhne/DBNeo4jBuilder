/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

import java.util.ArrayList;

/**
 *
 * @author benfrenco
 */
public class Parser_Info {

    private String desc;
    private String name;
    private String origin_url;
    private ArrayList<Parser_File_Entry> all_urls;
    String p_one_url = "(\\w+\\.zip)";

    public Parser_Info(String desc, String name, String origin_url) {
        this.desc = desc;
        this.name = name;
        this.origin_url = origin_url;
    }
    

    @Override
    public String toString() {
        return this.name; 
    }

    public ArrayList<Parser_File_Entry> getAll_urls() {
        if(all_urls==null){
            Parser_File_Entry original_one = new Parser_File_Entry(this.name, this.origin_url);
            all_urls = new ArrayList<>();
            all_urls.add(original_one);
        }
        return all_urls;
    }

    
    
    public void setAll_urls(ArrayList<Parser_File_Entry> all_urls) {
        this.all_urls = all_urls;
    }

    
    
    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public String getOrigin_url() {
        return origin_url;
    }

    public Parser_Info() {
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin_url(String origin_url) {
        this.origin_url = origin_url;
    }

    public void checking() {
        System.out.println(this.name + ":" + this.origin_url);
        if (!all_urls.isEmpty()) {

        }
    }

}
