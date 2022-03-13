package com.ucc.csbsafety;

public class Profile {
    public String FName,LName;
    public String UserImg;

    public String getFullname() {
        return FName +" "+LName;
    }

    public String getImage() {
        return UserImg;
    }
}
