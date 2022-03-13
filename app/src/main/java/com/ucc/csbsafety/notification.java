package com.ucc.csbsafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ucc.csbsafety.adapters.notifAdapter;
import com.ucc.csbsafety.adapters.symptomAdapter;

import java.util.ArrayList;
import java.util.List;

public class notification extends AppCompatActivity {
    BottomNavigationView botNav;
    RecyclerView recyclerView;
    notifAdapter adapter;
    List<String> titles;
    List<String> desc;
    List<String> dateAndTime;
    List<String> moreInfo;
    private FirebaseAuth user;
    TextView notifSymptoms,notifCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerView = findViewById(R.id.notifRecycler);
        botNav = findViewById(R.id.bottom_navigation);
        notifSymptoms = findViewById(R.id.notifSymptoms);
        notifCases = findViewById(R.id.notifCases);

        recyclerView.setFocusable(false);

        titles = new ArrayList<>();
        desc = new ArrayList<>();
        dateAndTime = new ArrayList<>();
        moreInfo = new ArrayList<>();

        user = FirebaseAuth.getInstance();// Initialize Firebase Auth

        String uid = user.getUid();

        //startService(new Intent(notification.this,RSSPullService.class));
        //finish();

//        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//
//        JobInfo jobInfo = new JobInfo.Builder(11, new ComponentName(this, RSSPullService.class))
//                // only add if network access is required
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .build();
//
//        jobScheduler.schedule(jobInfo);


//        Intent intent = new Intent(this, BackgroundService.class);
//        this.startService(intent);
//        sendBroadcast(intent);

        getCasesCount();
        getNotificationsOnFirebase(uid);

        //getDbChartData(uid,date);
        botNav.setSelectedItemId(R.id.report);//Set Home Selected
        //perform item seleted listener
        botNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    startActivity(new Intent(getApplicationContext()
                            ,HomeScreen.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.report:
                    return true;
                case R.id.scan:
                    startActivity(new Intent(getApplicationContext()
                            ,qrScan.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.info:
                    startActivity(new Intent(getApplicationContext()
                            , symptomsInfo.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.account:
                    startActivity(new Intent(getApplicationContext()
                            ,accountSettings.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
            }
            return false;
        });

    }
    @Override
    public void onBackPressed() {// ask user when he pressed back button
        new android.app.AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notification.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void getCasesCount() {
        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int casesCount = 0;
                int hasSymptomsCount = 0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String status = String.valueOf(postSnapshot.child("Status").getValue());
                    String symptoms = String.valueOf(postSnapshot.child("userSymptoms").getValue());
                    if (!symptoms.equals("null")) {
                        hasSymptomsCount++;
                        notifSymptoms.setText(String.valueOf(hasSymptomsCount));
                    }
                    if (status.equals("positive")) {
                        casesCount++;
                        notifCases.setText(String.valueOf(casesCount));
                    }

//                    Toast.makeText(notification.this, ""+status+symptoms, Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());//delay the code a for 1 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupRecyclerAdapter();
            }
        }, 2000);

    }

    private void getNotificationsOnFirebase(String uid) {
        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Notifications/"+uid);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String Date = String.valueOf(postSnapshot.child("Date").getValue());
                    String Description = String.valueOf(postSnapshot.child("Description").getValue());
                    String Title = String.valueOf(postSnapshot.child("Header").getValue());
                    String MoreInfo = String.valueOf(postSnapshot.child("OnclickInfo").getValue());
                    String Time = String.valueOf(postSnapshot.child("Time").getValue());

                    titles.add(Title);
                    desc.add(Description);
                    dateAndTime.add(Date+" "+Time);
                    moreInfo.add(MoreInfo);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());//delay the code a for 1 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    setupRecyclerAdapter();
                }
            }, 2000);

    }

    private void setupRecyclerAdapter() {//sets up the layout if recyclerView as  well as the adapter for Categories
        adapter = new notifAdapter(this,titles,desc,dateAndTime,moreInfo,notification.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);


    }

}