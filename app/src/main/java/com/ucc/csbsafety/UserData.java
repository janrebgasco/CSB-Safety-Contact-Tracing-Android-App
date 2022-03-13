package com.ucc.csbsafety;

public class UserData {

    public String LName;
    public String FName;
    public String status;
    public String userImg;
    public String Usertype;
    public String StudentNum;
    public String uid;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserData(String LName, String FName,String status,String userImg,String StudentNum, String Usertype,String uid) {
        this.LName = LName;
        this.FName = FName;
        this.status = status;
        this.userImg = userImg;
        this.StudentNum = StudentNum;
        this.Usertype = Usertype;
        this.uid = uid;
    }

}
