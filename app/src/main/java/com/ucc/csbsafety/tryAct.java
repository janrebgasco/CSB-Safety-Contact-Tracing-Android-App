package com.ucc.csbsafety;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class tryAct extends AppCompatActivity {
    private DatabaseReference mDatabase;
    Button btnScan;
    TextView txtQR;
    List<String> uids = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<String> status = new ArrayList<>();
    ValueEventListener valListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);

        btnScan = findViewById(R.id.btnScan);
        txtQR = findViewById(R.id.txtBoxQR);

        String uid = "XcOg6QzzvlcE95rxgVzS5O4rWho1";//replace with user.getUid()
        mDatabase = FirebaseDatabase.getInstance().getReference();//Realtime Database

        String dateNum = new SimpleDateFormat("M dd", Locale.getDefault()).format(new Date());//number format of date
        String date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());//

        mDatabase.child("ClosedContact").child(uid).child(dateNum).child("closedContactCount").setValue(0);
        mDatabase.child("ClosedContact").child(uid).child(dateNum).child("date").setValue(date);

        getCloseContact2(uid);



        btnScan.setOnClickListener(new View.OnClickListener() {//for qr scanner code
            @Override
            public void onClick(View v) {
                String qrText = txtQR.getText().toString();//room-establishment-department
                String[] separated = qrText.split("-");//room,establishment,department
                String establishment = separated[1];
                String dept = separated[2];
                String action = separated[3];



                if (!checkInternetConn()){
                    Toast.makeText(tryAct.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else if(action.equals("leave")){
                    removeUserToList(establishment,dept);
                }
                else{
                    getperonInsideRoom(establishment,dept,uid,dateNum);
                }
            }

            private void removeUserToList(String establishment,String dept) {
                mDatabase.child("room").child(establishment).child(dept).child(uid).removeValue();
            }

            private boolean checkInternetConn() {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        });
    }
    private String getTime() {
        String delegate = "hh:mm aaa";
        return (String) DateFormat.format(delegate,Calendar.getInstance().getTime());
    }
    private void insertUserContacts(String myUid,String date,String department,String establishment) {
        String currentime = getTime();
        String fullDate = new SimpleDateFormat("MMM dd, y", Locale.getDefault()).format(new Date());

        for (int i = 0; i < uids.size();i++){
            String UID = uids.get(i);
            String Time = time.get(i);
            String Status = status.get(i);

            InsideList person = new InsideList(UID,fullDate,Time,department,Status);
            MyInfo myInfo = new MyInfo(myUid,fullDate,currentime,department,"negative");//TODO :: replace negative with db data

            mDatabase.child("ClosedContact").child(myUid).child(date).child("ccList").child(UID).setValue(person);//insert close contact to user
            mDatabase.child("ClosedContact").child(UID).child(date).child("ccList").child(myUid).setValue(myInfo);//insert close contact to other users
            mDatabase.child("room").child(establishment).child(department).child(myUid).setValue(myInfo);//insert user to the room

            Toast.makeText(this, "Inserted "+UID +" to close contact.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getperonInsideRoom(String establishment, String department,String uid, String date) {
        Toast.makeText(tryAct.this, "Please wait....\nGetting list of person inside", Toast.LENGTH_SHORT).show();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("room/"+establishment+"/"+department);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot children : snapshot.getChildren()){
                    uids = new ArrayList<>();
                    time = new ArrayList<>();
                    status = new ArrayList<>();

                    uids.add(String.valueOf(children.child("uid").getValue()));
                    time.add(String.valueOf(children.child("timeIn").getValue()));
                    status.add(String.valueOf(children.child("status").getValue()));

                    insertUserContacts(uid, date, department, establishment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(tryAct.this, "An error occurred\n "+error, Toast.LENGTH_SHORT).show();
            }
        };
        ref.addListenerForSingleValueEvent(listener);
        valListener = listener;

    }


    private void getCloseContact2(String uid) {//for showing the close contact code

        TableLayout stk = findViewById(R.id.tblCloseContacts);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("ClosedContact/"+uid);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot numDate : snapshot.getChildren()){
                    //String date = String.valueOf(numDate.child("date").getValue());
                    Iterable<DataSnapshot> ds = numDate.child("ccList").getChildren();
                    for( DataSnapshot ccListChild : ds){
                        String name = String.valueOf(ccListChild.child("name").getValue());
                        String uid = String.valueOf(ccListChild.child("uid").getValue());
                        String date = String.valueOf(ccListChild.child("date").getValue());
                        String time = String.valueOf(ccListChild.child("time").getValue());
                        String establishment = String.valueOf(ccListChild.child("establishment").getValue());
                        String department = String.valueOf(ccListChild.child("department").getValue());
                        String status = String.valueOf(ccListChild.child("status").getValue());
                        //Toast.makeText(MainActivity.this, name +"\n"+ uid +"\n"+ date +"\n"+ time +"\n"+ establishment +"\n"+ department +"\n"+ status, Toast.LENGTH_LONG).show();

                        stk.removeAllViews();
                        //stk.refreshDrawableState();
                        // create a new TableRow
                        TableRow tr = new TableRow(tryAct.this);
                        // create a new TextView for showing xml data
                        TextView td = new TextView(tryAct.this);
                        // set the text to "text xx"
                        td.setText(uid);
                        td.setTextColor(Color.BLACK);
                        td.setPadding(0,10,0,0);
                        td.setGravity(Gravity.CENTER_HORIZONTAL);
                        td.setTextSize(12);
                        tr.addView(td);
                        TextView td2 = new TextView(tryAct.this);
                        td2.setText(date);
                        td2.setTextColor(Color.BLACK);
                        td2.setPadding(0,10,0,0);
                        td2.setGravity(Gravity.CENTER_HORIZONTAL);
                        td2.setTextSize(12);
                        tr.addView(td2);
                        TextView td3 = new TextView(tryAct.this);
                        td3.setText(time);
                        td3.setTextColor(Color.BLACK);
                        td3.setPadding(0,10,0,0);
                        td3.setGravity(Gravity.CENTER_HORIZONTAL);
                        td3.setTextSize(12);
                        tr.addView(td3);
                        TextView td4 = new TextView(tryAct.this);
                        td4.setText(department);
                        td4.setTextColor(Color.BLACK);
                        td4.setTextColor(Color.BLACK);
                        td4.setPadding(0,10,0,0);
                        td4.setGravity(Gravity.CENTER_HORIZONTAL);
                        td4.setTextSize(12);
                        tr.addView(td4);
                        TextView td5 = new TextView(tryAct.this);
                        td5.setText(status);
                        td5.setTextColor(Color.BLACK);
                        td5.setTextColor(Color.BLACK);
                        td5.setPadding(0,10,0,0);
                        td5.setGravity(Gravity.CENTER_HORIZONTAL);
                        td5.setTextSize(12);
                        tr.addView(td5);
                        stk.addView(tr);

                        //table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addValueEventListener(listener);
    }

    private void showBarGraphData(String uid,String date){
        String[] months = {"Oct 1", "Oct 2", "Oct 3", "Oct 4", "Oct 5", "Oct 6"};

        final BarChart mChart = findViewById(R.id.homeBarGraph);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);

        XAxis xaxis = mChart.getXAxis();
        xaxis.setDrawGridLines(false);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(1f);
        xaxis.setDrawLabels(true);
        xaxis.setDrawAxisLine(false);
        xaxis.setValueFormatter(new IndexAxisValueFormatter(months));

        YAxis yAxisLeft = mChart.getAxisLeft();
        yAxisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
//        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawAxisLine(false);
//        yAxisLeft.setEnabled(false);

        mChart.getAxisRight().setEnabled(false);

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);


        final ArrayList<BarEntry> valueSet1 = new ArrayList<>();//bar number data

        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("ClosedContact/"+uid+"/"+date);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClosedContacts closedContacts = dataSnapshot.getValue(ClosedContacts.class);
                String ccCount = closedContacts.getCloseContactCount();
                int contactCount = Integer.parseInt(ccCount);
                Toast.makeText(tryAct.this, String.valueOf(ccCount), Toast.LENGTH_LONG).show();
                if (contactCount == 0){
                    for (int i = 0; i < 6; ++i) {
                        BarEntry entry = new BarEntry(i, contactCount);
                        valueSet1.add(entry);
                    }
                }

                List<IBarDataSet> dataSets = new ArrayList<>();
                BarDataSet barDataSet = new BarDataSet(valueSet1, " ");
                barDataSet.setColor(Color.BLUE);
                barDataSet.setDrawValues(false);
                dataSets.add(barDataSet);

                BarData data = new BarData(dataSets);
                mChart.setData(data);
                mChart.invalidate();
                //else get the array of closed contacts

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }

        });

    }
}