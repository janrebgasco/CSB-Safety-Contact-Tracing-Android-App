package com.ucc.csbsafety;

public class ProfileHome {
    public String FName,LName;
    public String UserImg;
    public String Temp;

    public String getFullname() {
        return FName +" "+LName;
    }

    public String getImage() {
        return UserImg;
    }

}
