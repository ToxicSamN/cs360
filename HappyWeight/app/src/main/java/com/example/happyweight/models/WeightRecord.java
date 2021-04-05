package com.example.happyweight.models;

import java.util.Date;

public class WeightRecord {

    // Object properties
    private int id;
    private String user;
    private String date;
    private int weight;

    // getters
    public int getId(){
        return this.id;
    }

    public int getWeight(){
        return this.weight;
    }

    public String getDate() {
        return this.date;
    }

    public String getUser() {
        return this.user;
    }

    // setters
    public void setId(int id){
        this.id = id;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void setDate(String sDate) { this.date = sDate; }

    public void setUser(String user) {
        this.user = user;
    }
}
