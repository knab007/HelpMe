package com.software.uottawa.helpme;

import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by Samir T.
 */


public class Service {
    /* Attributes */
    private String mId;
    private String mTitle;
    private String mCreatorId;
    private String mCreatorName;
    private String mDescription;
    private String mHourlyRate;
    private List<String> mAssignedPS;
    private List<String> mRessources;
    private String mInstruction;

    DatabaseReference databaseNotes;
    // DatabaseReference databaseFeed;

    ListView listViewInst;
    ListView listViewFeed;

    List<Note> inst;
    List<Note> feed;


    public Service() {
        //SET BY DEFAULT BY ECLIPSE, CHANGE IF NECESSARY
    }

    /*
     *	getters et setters
     */

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    public String getCreatorName() {
        return mCreatorName;
    }

    public void setCreatorName(String creatorName) {
        this.mCreatorName = creatorName;
    }

    public String getCreatorId() {
        return mCreatorId;
    }

    public void setCreatorId(String creatorId) {
        this.mCreatorId = creatorId;
    }

    public String getInstruction() {
        return mInstruction;
    }

    public void setInstruction(String instruction) {
        mInstruction = instruction;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getRate(){ return mHourlyRate; }

    public void setRate(String HourlyRate){
        mHourlyRate = HourlyRate;
    }

    public List<String> getAssignedPS() {
        return mAssignedPS;
    }

    public void setAssignedPS(List<String> assignedPS) {
        mAssignedPS = assignedPS;
    }

    public List<String> getRessources() {
        return mRessources;
    }

    public void setRessources(List<String> ressources) {
        mRessources = ressources;
    }

}
