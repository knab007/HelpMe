package com.software.uottawa.helpme;

/**
 * Created by Samir T..
 */

public class Note{

    private String id;	// Unique String servant de cle/identifiant
    private String tacheId;
    private String creator;
    private String comment;


    public Note(String i,String ti, String cr, String co){
        this.id = i;
        this.tacheId = ti;
        this.creator = cr;
        this.comment = co;
    }

    public Note(String cr, String co){
        this.creator = cr;
        this.comment = co;
    }

    public Note(){

    }
    // Methodes
    protected String getId(){ return id; }

    protected String getCreator(){ return creator; }

    protected String getComment(){ return comment; }

    protected void editComment(String note){ comment = note; }

    protected String getTacheId(){ return tacheId; }
}
