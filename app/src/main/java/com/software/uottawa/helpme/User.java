package com.software.uottawa.helpme;
import android.widget.RatingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samir T.
 */

public class User {

    /* Attributes */
    private String mId;
    private String mFirstName;
    private String mLastName;
    private String mEmail;

    private String typeOfUser;


    private int mPoints;
    private List<Double>  mListRating;
    private Double rating;

    private List<String> mAssignedServices;
    private List<String> mDisponibility;



    /* Constructeurs */
    public User(String mId, String mFirstName, String mLastName, String mEmail, String type) {
        this.mId = mId;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mEmail = mEmail;
        this.typeOfUser = type;

        this.mPoints = 0;
        this.rating = 0.0;
        this.mAssignedServices = new ArrayList<>();
        this.mDisponibility = new ArrayList<>();
        this.mListRating = new ArrayList<>();

        if(mListRating != null) {
            Double somme = mListRating.get(0);
            if(mListRating.size()>=2) {
                for (int i = 1; i < mListRating.size(); i++) {
                    somme = somme + mListRating.get(i);
                }
                rating = somme / mListRating.size();
            }
            rating = somme;
        }
    }

    public User() {
        //SET BY DEFAULT BY ECLIPSE, CHANGE IF NECESSARY
    }


    /*
	*	getters et setters
	*/

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }


    public String getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public List<Double> getListRating() { return mListRating; }

    public void setListRating(List<Double> ListRating) { this.mListRating = ListRating; }

    public Double getRating() { return rating; }

    public void setRating(Double rating) { this.rating = rating; }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        this.mPoints = points;
    }

    public List<String> getAssignedServices() {
        return mAssignedServices;
    }

    public void setAssignedServices(List<String> assignedServices) {
        this.mAssignedServices = assignedServices;
    }

    public List<String> getDisponibility() { return mDisponibility; }

    public void setDisponibility(List<String> days) {
        this.mDisponibility = days;
    }


}

