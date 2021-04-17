package com.example.happyweight.models;

public class UserRecord {

    // Object properties
    private int id;
    private String userID;
    private String firstName;
    private String lastName;
    private int SMS;
    private String telNo;

    public UserRecord(){
        this.id = -1;
        this.userID = "";
        this.firstName = "";
        this.lastName = "";
        this.SMS = -1;
        this.telNo = null;
    }

    // getters
    public int getId(){
        return this.id;
    }

    public String getUserID(){
        return this.userID;
    }

    public int getSMS(){
        return this.SMS;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getTelNo() { return  this.telNo; }

    // setters
    public void setId(int id){
        this.id = id;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setSMS(int enabled){
        this.SMS = enabled;
    }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTelNo(String telNo) { this.telNo = telNo; }
}
