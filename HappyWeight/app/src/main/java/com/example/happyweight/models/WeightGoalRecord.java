package com.example.happyweight.models;

public class WeightGoalRecord {
    // Object properties
    private int id;
    private String user;
    private int weight;

    // getters
    public int getId(){
        return this.id;
    }

    public int getWeight(){
        return this.weight;
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

    public void setUser(String user) {
        this.user = user;
    }
}
