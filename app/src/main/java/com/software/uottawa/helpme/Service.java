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
    //Dans le cas où on a plus d'1 admin et on veut garder la trace de l'admin qui a crée le service
    private String mCreatorName;
    private String mDescription;
    private String mHourlyRate;
    private List<String> mAssignedUsers;

    private String mResource;
    private String mInstruction;


    public Service() {
        //SET BY DEFAULT BY ECLIPSE, CHANGE IF NECESSARY
    }

    public Service(String mId, String mTitle,String mCreatorId,String mCreatorName,String mDescription, String mHourlyRate)
    {
        this.mId=mId;
        this.mTitle=mTitle;
        this.mCreatorId=mCreatorId;
        this.mCreatorName=mCreatorName;
        this.mDescription=mDescription;
        this.mHourlyRate=mHourlyRate;
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

    public List<String> getAssignedUsers() {
        return mAssignedUsers;
    }

    public void setAssignedUsers(List<String> assignedUsers) {
        mAssignedUsers = assignedUsers;
    }

    public String getResource() { return mResource;   }

    public void setResource(String resource) { this.mResource = resource;    }

}
