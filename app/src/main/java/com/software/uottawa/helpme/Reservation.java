package com.software.uottawa.helpme;

import java.util.List;

public class Reservation {
    //All users in this class must be HomeOwner du to constructor parameters
    private String mId;
    private String mUserId;
    private String mServiceId;

    private String mUsername;
    private String mEmail;
    private String mServiceName;
    private String mDate;
    private  String mResource;
    private int mUserAssigned;


    public Reservation(){}

    public Reservation(String Id, String serviceId, int userAssigned, String userId, String username, String serviceName, String date, String resource, String email){
        mId = Id;
        mServiceId = serviceId;
        mUserId = userId;
        mUserAssigned = userAssigned;
        mUsername = username;
        mEmail = email;
        mServiceName = serviceName;
        mDate = date;
        mResource = resource;

    }

    public String getId() { return mId; }

    public void setId(String id) { this.mId = id; }

    public String getUserId() { return mUserId; }

    public void setUserId(String userId) { this.mUserId = userId; }

    public String getServiceId() { return mServiceId; }

    public void setServiceId(String serviceId) { this.mServiceId = serviceId; }

    public String getUsername() { return mUsername; }

    public void setUsername(String username) { this.mUsername = username; }

    public String getEmail() { return mEmail; }

    public void setEmail(String email) { this.mEmail = email; }

    public String getServiceName() { return mServiceName; }

    public void setServiceName(String serviceName) { this.mServiceName = serviceName; }

    public String getDate() { return mDate; }

    public void setDate(String date) { this.mDate = date; }

    public String getResource() { return mResource; }

    public void setResource(String resource) { this.mResource = resource; }

    public int getUserAssigned() { return mUserAssigned; }

    public void setUserAssigned(int userAssigned) {
        this.mUserAssigned = userAssigned;
    }

}
