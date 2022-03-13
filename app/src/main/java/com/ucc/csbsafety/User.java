package com.ucc.csbsafety;

public class User {
    public String FName;
    public String LName;
    public String Email;
    public String Status;
    public String Usertype;
    public String StudentNum;

    public User(String firstname, String lastname, String email,String status,String StudentNum, String Usertype) {
        this.FName = firstname;
        this.LName = lastname;
        this.Email = email;
        this.Status = status;
        this.StudentNum = StudentNum;
        this.Usertype = Usertype;
    }
}
