package com.example.happyweight.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginModel {
    private static final String collection = "users";
    private static final String KEY_USERID = "userID";
    private static final String KEY_FNAME = "firstName";
    private static final String KEY_LNAME = "lastName";

    private final FirebaseFirestore db;

    private String userID;
    private String firstName;
    private String lastName;

    public LoginModel(){
        this.db = FirebaseFirestore.getInstance();
    }
    public LoginModel(String userID){
        this.userID = userID;
        this.db = FirebaseFirestore.getInstance();
        this.firstName = "";
        this.lastName = "";
    }

    public String getUserID(){
        return this.userID;
    }
    public String getFirstName(){
        return this.firstName;
    }
    public String getLastName(){
        return this.lastName;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void getData(){
        LoginModel tmpModel = this;
        this.db.collection(collection).document(this.userID).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LoginModel", e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tmpModel.userID = documentSnapshot.get(LoginModel.KEY_USERID).toString();
                        tmpModel.firstName = documentSnapshot.get(LoginModel.KEY_FNAME).toString();
                        tmpModel.lastName = documentSnapshot.get(LoginModel.KEY_LNAME).toString();
                    }
                });
        this.userID = tmpModel.userID;
        this.firstName = tmpModel.firstName;
        this.lastName = tmpModel.lastName;
    }

    public void setData(){
        this.db.collection(collection).document(this.userID).set(this.prepareDocument())
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LoginModel", e.toString());
            }
        });
    }

    private Map<String, String> prepareDocument(){
        Map<String, String> doc = new HashMap<>();

        doc.put(KEY_USERID, this.userID);
        doc.put(KEY_FNAME, this.firstName);
        doc.put(KEY_LNAME, this.lastName);

        return doc;
    }

}
