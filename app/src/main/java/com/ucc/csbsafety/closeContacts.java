package com.ucc.csbsafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class closeContacts extends AppCompatActivity {
    FirebaseAuth user;
    List<String> exposureDates = new ArrayList<>();
    String startExpDate,endExpDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_contacts);

        exposureDates = new ArrayList<>();
        initializeDays();
        user = FirebaseAuth.getInstance();
        String uid = user.getUid();
        getCloseContacts(uid);

    }
    private void initializeDays() {
        SimpleDateFormat sd = new SimpleDateFormat("MMM dd");
        SimpleDateFormat formatter = new SimpleDateFormat("y-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            Date date = Calendar.getInstance().getTime();
            String formattedDate = formatter.format(date);

            startDate = formatter.parse(formattedDate);
            endDate = formatter.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.add(Calendar.DAY_OF_YEAR, -13);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.add(Calendar.DAY_OF_YEAR, 1);

        startExpDate = sd.format(start.getTime());
        endExpDate = sd.format(end.getTime());

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

            exposureDates.add(formatter.format(date));

            //Toast.makeText(getApplicationContext(), ""+formatter.format(date), Toast.LENGTH_SHORT).show();
        }
    }



    private void getCloseContacts(String uid) {//for showing the close contact code

        TableLayout stk = findViewById(R.id.tblCloseContacts);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;


        DatabaseReference ref = database.getReference("ClosedContact/"+uid);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int[] finalJ = {0};
                final int[] i = {0};
                for (int j = 0;j <= exposureDates.size()-1;j++) {

                    DatabaseReference keyRef = database.getReference("ClosedContact/" + uid + "/" + exposureDates.get(j));


                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            long ccListCount = dataSnapshot.child("ccList").getChildrenCount();
                            String dateOfExposure = String.valueOf(dataSnapshot.child("date").getValue());

                            boolean childExist = !dateOfExposure.equals("null");
                            if (childExist) {

                                Iterable<DataSnapshot> ds = dataSnapshot.child("ccList").getChildren();
                                for( DataSnapshot ccListChild : ds){
                                    i[0]++;
                                    String name = String.valueOf(ccListChild.child("name").getValue());
                                    String uid = String.valueOf(ccListChild.child("uid").getValue());
                                    String date = String.valueOf(ccListChild.child("date").getValue());
                                    String time = String.valueOf(ccListChild.child("time").getValue());
                                    String establishment = String.valueOf(ccListChild.child("establishment").getValue());
                                    String department = String.valueOf(ccListChild.child("department").getValue());
                                    String status = String.valueOf(ccListChild.child("status").getValue());
                                    //Toast.makeText(MainActivity.this, name +"\n"+ uid +"\n"+ date +"\n"+ time +"\n"+ establishment +"\n"+ department +"\n"+ status, Toast.LENGTH_LONG).show();




                                    //stk.removeAllViews();
                                    //stk.refreshDrawableState();
                                    // create a new TableRow
                                    TableRow tr = new TableRow(closeContacts.this);

                                    // create a new TextView for showing xml data
                                    TextView tdid = new TextView(closeContacts.this);
                                    // set the text to "text xx"
                                    tdid.setText(String.valueOf(i[0]));
                                    tdid.setTextColor(Color.BLACK);
                                    tdid.setPadding(10,10,10,10);
                                    tdid.setGravity(Gravity.CENTER_HORIZONTAL);
                                    tdid.setTextSize(12);
                                    tdid.setWidth(40);
                                    tdid.setHeight(96);
                                    //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //td.setLayoutParams(params);
                                    checkIfPositive(uid,tdid);
                                    tr.addView(tdid);

                                    // create a new TextView for showing xml data
                                    TextView td = new TextView(closeContacts.this);
                                    // set the text to "text xx"
                                    td.setText(uid);
                                    td.setTextColor(Color.BLACK);
                                    td.setPadding(10,10,10,10);
                                    td.setGravity(Gravity.CENTER_HORIZONTAL);
                                    td.setTextSize(12);
                                    td.setWidth(320);//350
                                    td.setHeight(96);
                                    //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //td.setLayoutParams(params);
                                    checkIfPositive(uid,td);
                                    tr.addView(td);

                                    TextView td2 = new TextView(closeContacts.this);
                                    td2.setText(date);
                                    td2.setTextColor(Color.BLACK);
                                    td2.setPadding(10,10,10,10);
                                    td2.setGravity(Gravity.CENTER_HORIZONTAL);
                                    td2.setTextSize(12);
                                    td2.setWidth(210);
                                    td2.setHeight(96);
                                    //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //td.setLayoutParams(params);
                                    checkIfPositive(uid,td2);
                                    tr.addView(td2);

                                    TextView td3 = new TextView(closeContacts.this);
                                    td3.setText(time);
                                    td3.setTextColor(Color.BLACK);
                                    td3.setPadding(10,10,10,10);
                                    td3.setGravity(Gravity.CENTER_HORIZONTAL);
                                    td3.setTextSize(12);
                                    td3.setWidth(160);
                                    td3.setHeight(96);
                                    //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //td.setLayoutParams(params);
                                    checkIfPositive(uid,td3);
                                    tr.addView(td3);

                                    TextView td4 = new TextView(closeContacts.this);
                                    td4.setText(department);
                                    td4.setTextColor(Color.BLACK);
                                    td4.setPadding(10,10,10,10);
                                    td4.setGravity(Gravity.CENTER_HORIZONTAL);
                                    td4.setTextSize(12);
                                    td4.setWidth(160);
                                    td4.setHeight(96);
                                    //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //td.setLayoutParams(params);
                                    checkIfPositive(uid,td4);
                                    tr.addView(td4);

                                    TextView td5 = new TextView(closeContacts.this);
                                    getUserStatus(uid,td5);
                                    td5.setTextColor(Color.BLACK);
                                    td5.setPadding(10,10,10,10);
                                    td5.setGravity(Gravity.CENTER_HORIZONTAL);
                                    td5.setTextSize(12);
                                    td5.setWidth(180);
                                    td5.setHeight(96);
                                    //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //td.setLayoutParams(params);
                                    checkIfPositive(uid,td5);
                                    tr.addView(td5);
                                    stk.addView(tr);

                                    //table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    keyRef.addListenerForSingleValueEvent(eventListener);
                }



//                int i = 0;
//                for( DataSnapshot numDate : snapshot.getChildren()){
//                    //String date = String.valueOf(numDate.child("date").getValue());
//                    Iterable<DataSnapshot> ds = numDate.child("ccList").getChildren();
//                    for( DataSnapshot ccListChild : ds){
//                        i++;
//                        String name = String.valueOf(ccListChild.child("name").getValue());
//                        String uid = String.valueOf(ccListChild.child("uid").getValue());
//                        String date = String.valueOf(ccListChild.child("date").getValue());
//                        String time = String.valueOf(ccListChild.child("time").getValue());
//                        String establishment = String.valueOf(ccListChild.child("establishment").getValue());
//                        String department = String.valueOf(ccListChild.child("department").getValue());
//                        String status = String.valueOf(ccListChild.child("status").getValue());
//                        //Toast.makeText(MainActivity.this, name +"\n"+ uid +"\n"+ date +"\n"+ time +"\n"+ establishment +"\n"+ department +"\n"+ status, Toast.LENGTH_LONG).show();
//
//
//
//
//                        //stk.removeAllViews();
//                        //stk.refreshDrawableState();
//                        // create a new TableRow
//                        TableRow tr = new TableRow(closeContacts.this);
//
//                        // create a new TextView for showing xml data
//                        TextView tdid = new TextView(closeContacts.this);
//                        // set the text to "text xx"
//                        tdid.setText(String.valueOf(i));
//                        tdid.setTextColor(Color.BLACK);
//                        tdid.setPadding(10,10,10,10);
//                        tdid.setGravity(Gravity.CENTER_HORIZONTAL);
//                        tdid.setTextSize(12);
//                        tdid.setWidth(40);
//                        tdid.setHeight(96);
//                        //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        //td.setLayoutParams(params);
//                        checkIfPositive(uid,tdid);
//                        tr.addView(tdid);
//
//                        // create a new TextView for showing xml data
//                        TextView td = new TextView(closeContacts.this);
//                        // set the text to "text xx"
//                        td.setText(uid);
//                        td.setTextColor(Color.BLACK);
//                        td.setPadding(10,10,10,10);
//                        td.setGravity(Gravity.CENTER_HORIZONTAL);
//                        td.setTextSize(12);
//                        td.setWidth(320);//350
//                        td.setHeight(96);
//                        //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        //td.setLayoutParams(params);
//                        checkIfPositive(uid,td);
//                        tr.addView(td);
//
//                        TextView td2 = new TextView(closeContacts.this);
//                        td2.setText(date);
//                        td2.setTextColor(Color.BLACK);
//                        td2.setPadding(10,10,10,10);
//                        td2.setGravity(Gravity.CENTER_HORIZONTAL);
//                        td2.setTextSize(12);
//                        td2.setWidth(210);
//                        td2.setHeight(96);
//                        //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        //td.setLayoutParams(params);
//                        checkIfPositive(uid,td2);
//                        tr.addView(td2);
//
//                        TextView td3 = new TextView(closeContacts.this);
//                        td3.setText(time);
//                        td3.setTextColor(Color.BLACK);
//                        td3.setPadding(10,10,10,10);
//                        td3.setGravity(Gravity.CENTER_HORIZONTAL);
//                        td3.setTextSize(12);
//                        td3.setWidth(160);
//                        td3.setHeight(96);
//                        //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        //td.setLayoutParams(params);
//                        checkIfPositive(uid,td3);
//                        tr.addView(td3);
//
//                        TextView td4 = new TextView(closeContacts.this);
//                        td4.setText(department);
//                        td4.setTextColor(Color.BLACK);
//                        td4.setPadding(10,10,10,10);
//                        td4.setGravity(Gravity.CENTER_HORIZONTAL);
//                        td4.setTextSize(12);
//                        td4.setWidth(160);
//                        td4.setHeight(96);
//                        //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        //td.setLayoutParams(params);
//                        checkIfPositive(uid,td4);
//                        tr.addView(td4);
//
//                        TextView td5 = new TextView(closeContacts.this);
//                        getUserStatus(uid,td5);
//                        td5.setTextColor(Color.BLACK);
//                        td5.setPadding(10,10,10,10);
//                        td5.setGravity(Gravity.CENTER_HORIZONTAL);
//                        td5.setTextSize(12);
//                        td5.setWidth(180);
//                        td5.setHeight(96);
//                        //td.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        //td.setLayoutParams(params);
//                        checkIfPositive(uid,td5);
//                        tr.addView(td5);
//                        stk.addView(tr);
//
//                        //table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        ref.addValueEventListener(listener);
    }

    public void ccBack(View view) {
        super.onBackPressed();
    }

    private void getUserStatus(String uid,TextView td5){


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("users/"+uid);
        ref.child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String Status = snapshot.getValue().toString();

                td5.setText(Status);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void checkIfPositive(String uid,TextView td5){


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("users/"+uid);
        ref.child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String Status = snapshot.getValue().toString();
                if (Status.equals("positive")){
                    td5.setBackground(ContextCompat.getDrawable(closeContacts.this, R.drawable.cellborder_red));
                }
                else{
                    td5.setBackground(ContextCompat.getDrawable(closeContacts.this, R.drawable.cellborder_black));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}