package com.ucc.csbsafety;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    String FName;
    String LName;
    String email;
    int age;
    String gender;
    String address;
    String establishment;
    String UserImg;

    public Post(){
        //default method
    }
    public Post(String fName, String lName, int age, String gender, String address,String image) {
        this.FName = fName;
        this.LName = lName;
        //this.email = email;
        this.age = age;
        this.gender = gender;
        this.address = address;
        //this.establishment = establishment;
        this.UserImg = image;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("FName", FName);
        result.put("LName", LName);
        //result.put("Email", email);
        result.put("Age", age);
        result.put("Gender", gender);
        result.put("Address", address);
        //result.put("Establishment", establishment);
        result.put("UserImg", UserImg);

        return result;
    }

    public void updateAcc(String fName, String lName, String email, int age, String gender, String address, String establishment) {
        this.FName = fName;
        this.LName = lName;
        //this.email = email;
        this.age = age;
        this.gender = gender;
        this.address = address;
        //this.establishment = establishment;
        this.UserImg = UserImg;
    }
}