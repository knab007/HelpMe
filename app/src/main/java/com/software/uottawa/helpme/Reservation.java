package com.software.uottawa.helpme;

public class Reservation {
    //All users in this class must be HomeOwner du to constructor parameters
    private String mId;
    private String mHomeOwnerId;
    private String mHomeOwnerName;

    private String mPSAssignedId;
    private String mPsAssignedName;
    private int mPsAssignedRating;
    private String mPsAssignedEmail;

    private String mServiceId;
    private String mServiceName;
    private String mServiceDescription;
    private String mDate;
    private  String mResource;

    public Reservation(){}

    public Reservation(String Id,  String homeOwnerId, String homeOwnerName, String psAssignedId, String psAssignedName, String psAssignedEmail, int psAssignedRating, String serviceId,String serviceName, String serviceDescription, String date, String resource){
        mId = Id;
        mHomeOwnerId = homeOwnerId;
        mHomeOwnerName = homeOwnerName;

        mPSAssignedId = psAssignedId;
        mPsAssignedName = psAssignedName;
        mPsAssignedEmail = psAssignedEmail;
        mPsAssignedRating = psAssignedRating;

        mServiceId = serviceId;
        mServiceName = serviceName;
        mServiceDescription = serviceDescription;
        mDate = date;
        mResource = resource;

    }

    public String getId() { return mId; }

    public void setId(String id) { this.mId = id; }

    public String getHomeOwnerId() { return mHomeOwnerId; }

    public void setHomeOwnerId(String userId) { this.mHomeOwnerId = userId; }

    public String getHomeOwnerName() { return mHomeOwnerName; }

    public void setHomeOwnerName(String homeOwnerName) { this.mHomeOwnerName = homeOwnerName; }

    public String getPsAssignedName() { return mPsAssignedName; }

    public void setPsAssignedName(String psAssignedName) { this.mPsAssignedName = psAssignedName; }

    public int getPsAssignedRating() { return mPsAssignedRating; }

    public void setPsAssignedRating(int psAssignedRating) { this.mPsAssignedRating = psAssignedRating; }

    public String getPsAssignedEmail() { return mPsAssignedEmail; }

    public void setEmail(String psAssignedEmail) { this.mPsAssignedEmail = psAssignedEmail; }

    public String getServiceId() { return mServiceId; }

    public void setServiceId(String serviceId) { this.mServiceId = serviceId; }

    public String getServiceName() { return mServiceName; }

    public void setServiceName(String serviceName) { this.mServiceName = serviceName; }

    public String getServiceDescription() { return mServiceDescription; }

    public void setServiceDescription(String serviceDescription) {
        this.mServiceDescription = serviceDescription;
    }

    public String getDate() { return mDate; }

    public void setDate(String date) { this.mDate = date; }

    public String getResource() { return mResource; }

    public void setResource(String resource) { this.mResource = resource; }

    public String getPSAssignedId() { return mPSAssignedId; }

    public void setPSAssignedId(String psAssignedId) { this.mPSAssignedId = psAssignedId; }

}
