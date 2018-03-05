/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.master.neo4jdbbuilder;

/**
 *
 * @author benfrenco
 */
public class Parser_File_Entry {

    private String name;
    private String url;
    private String download_url;

    public String size;

    public Parser_File_Entry(String name, String url) {
        this.name = name;
        this.url = url;
        if(url.contains(Properties.canada_url_check)){
            this.download_url = Properties.canada_download_url;
        }
    }

    public void setUrl(String new_url) {
        this.url = new_url;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        if (size instanceof String) {
            return size;
        } else {
            size = "";
            return size;
        }
    }

    public void print() {
        System.out.println(name + "," + size);
    }

    public String getDownload_url() {
        return download_url;
    }
    
    

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

}
