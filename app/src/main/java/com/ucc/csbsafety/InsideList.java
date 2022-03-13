package com.ucc.csbsafety;

public class InsideList {

    public String uid;
    public String date;
    public String time;
    public String status;
    public String department;

    public InsideList() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public InsideList(String uid, String date, String time,String department, String status) {
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.department = department;
        this.status = status;
    }

}
