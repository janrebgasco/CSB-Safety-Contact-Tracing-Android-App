package com.ucc.csbsafety;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeScreen extends AppCompatActivity {
    BottomNavigationView botNav;
    private FirebaseAuth user;
    private DatabaseReference mDatabase;
    TextView totalContacts,dispName,dispDate,userConditionTxt,userSymptomsText,IDNumber,txtTemp;
    String date, date1;
    ToggleButton toggleNegative,togglePositive;
    EditText txtBoxCaseNum;
    Button btnSubmit;
    CardView positiveCardView;
    ImageView userCondition;
    List<String> exposureDates = new ArrayList<>();
    String startExpDate,endExpDate;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        botNav = findViewById(R.id.bottom_navigation);
        totalContacts = findViewById(R.id.totalccCount);
        dispName = findViewById(R.id.homeUserName);
        dispDate = findViewById(R.id.exposureDate);
        toggleNegative = findViewById(R.id.toggleNegative);
        togglePositive = findViewById(R.id.togglePositive);
        txtBoxCaseNum = findViewById(R.id.txtBoxCaseNumber);
        btnSubmit = findViewById(R.id.btnSubmit);
        positiveCardView = findViewById(R.id.positiveCardView);
        userCondition = findViewById(R.id.imageView7);
        userConditionTxt = findViewById(R.id.textView12);
        userSymptomsText = findViewById(R.id.textView50);
        IDNumber = findViewById(R.id.txtBoxIdnum);
        txtTemp = findViewById(R.id.homeTemp);

        user = FirebaseAuth.getInstance();// Initialize Firebase Auth
        mDatabase = FirebaseDatabase.getInstance().getReference();//Realtime Database

        final FirebaseDatabase database = FirebaseDatabase.getInstance();// Get a reference to your user
        exposureDates = new ArrayList<>();

        String uid = user.getUid();

        dispUserName(database);
        initializeDays();//looping through dates, CAN BE USED IN 14 DAYS EXPOSURE
        dispDateOfExposure(uid);
        initializeDate(uid);

        showBarGraphData(uid);
        getTotalContactCount(uid);
        checkForUpdates();

        positiveCardView.setVisibility(View.GONE);


        toggleNegative.setOnCheckedChangeListener((compoundButton, isNegative) -> {
            if (isNegative){
                togglePositive.setChecked(false);
                positiveCardView.setVisibility(View.GONE);
            }
        });

        togglePositive.setOnCheckedChangeListener((compoundButton, isPositive) -> {
            if (isPositive){
                toggleNegative.setChecked(false);
                positiveCardView.setVisibility(View.VISIBLE);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caseNumber = txtBoxCaseNum.getText().toString();
                String submittedID = IDNumber.getText().toString();

                new AlertDialog.Builder(HomeScreen.this)
                        .setTitle("Case Number confirmation")
                        .setMessage("Are you sure you want to submit this case number?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                mDatabase.child("positiveCases").child(uid).child("CaseNumber").setValue(caseNumber);
                                mDatabase.child("positiveCases").child(uid).child("IDNumber").setValue(submittedID);
                                mDatabase.child("positiveCases").child(uid).child("uid").setValue(uid);
                                insertFullname(database);

                                Toast.makeText(getApplicationContext(), "Your test result has been submitted.\nWait for verification.", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        checkUserStatus(uid);
        getUserSymptoms(uid);



        //getDbChartData(uid,date);
        botNav.setSelectedItemId(R.id.home);//Set Home Selected
        //perform item seleted listener
        botNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    return true;
                case R.id.report:
                    startActivity(new Intent(getApplicationContext()
                            ,notification.class));
                    overridePendingTransition(0,0);
                    finish();
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
                        HomeScreen.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void checkForUpdates() {
        //UPDATING THE APP
        AppUpdater appUpdater = new AppUpdater(this);

        appUpdater.setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/janrebgasco/csb-update-log.json/main/update-changelog.json")
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available of this app")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update")
                .setIcon(R.drawable.logo) // Notification icon
                .setCancelable(false)
                .setButtonDoNotShowAgain(null); // Dialog could not be dismissable;

        appUpdater.start();
    }

    private void updateChecker() {
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                //.setUpdateFrom(UpdateFrom.AMAZON)
                //.setUpdateFrom(UpdateFrom.GITHUB)
                //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                //...
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {

                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                    }
                });
        appUpdaterUtils.start();
    }

    private void checkUserStatus(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String userStatus = String.valueOf(dataSnapshot.child("Status").getValue());
                    String userSymptoms = String.valueOf(dataSnapshot.child("userSymptoms").getValue());
                    String userTemp = String.valueOf(dataSnapshot.child("Temp").getValue());

                    if(!userTemp.equals("null")) {
                        txtTemp.setText("Temperature: \n"+userTemp+"°");
                    }

                    if(!userSymptoms.equals("null")){
                        toggleNegative.setChecked(true);
                        togglePositive.setChecked(false);
                        userCondition.setImageResource(R.drawable.unwell_status);
                        userConditionTxt.setText("Poor Condition");
                    }else if (userStatus.equals("negative")){
                        toggleNegative.setChecked(true);
                        togglePositive.setChecked(false);
                        userCondition.setImageResource(R.drawable.active_status);
                        userConditionTxt.setText("Good Condition");
                    }else if(userStatus.equals("positive")){
                        togglePositive.setChecked(true);
                        toggleNegative.setChecked(false);
                        userCondition.setImageResource(R.drawable.red_status);
                        userConditionTxt.setText("Bad Condition");
                        positiveCardView.setVisibility(View.GONE);
                        toggleNegative.setEnabled(false);
                    }else{
                        toggleNegative.setChecked(true);
                        togglePositive.setChecked(false);
                        userCondition.setImageResource(R.drawable.active_status);
                        userConditionTxt.setText("Good Condition");
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void dispDateOfExposure(String uid) {
        String endate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());

        dispDate.setText(startExpDate + "-" +endate);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClosedContact/"+uid);
//        Query queryUid = ref.orderByKey().limitToFirst(1);
//        queryUid.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//                    String startDate = String.valueOf(postSnapshot.child("date").getValue());
//                    String endDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());
//
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void initializeDate(String uid) {
        date = new SimpleDateFormat("y-MM-dd", Locale.getDefault()).format(new Date());
        date1 = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());

        mDatabase.child("ClosedContact").child(uid).child(date).child("closedContactCount").setValue(0);
        mDatabase.child("ClosedContact").child(uid).child(date).child("date").setValue(date1);
    }

    private void dispUserName(FirebaseDatabase database) {
        String id = user.getUid();
        //displaying Fullname after getting it to realtime db
        DatabaseReference ref = database.getReference("users/"+id);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                assert profile != null;
                dispName.setText(profile.getFullname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void insertFullname(FirebaseDatabase database) {
        String id = user.getUid();
        //displaying Fullname after getting it to realtime db
        DatabaseReference ref = database.getReference("users/"+id);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                assert profile != null;
                mDatabase.child("positiveCases").child(id).child("Fullname").setValue(profile.getFullname());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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

    private void getTotalContactCount(String uid) {

        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query ref = database.getReference("ClosedContact/"+uid).limitToLast(14);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int[] totalccCount = {0};
                for (int j = 0;j <= exposureDates.size()-1;j++) {

                    DatabaseReference keyRef = database.getReference("ClosedContact/"+ uid + "/"+exposureDates.get(j));
                    String name = dataSnapshot.child("name").getValue(String.class);


                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long childCount = dataSnapshot.child("ccList").getChildrenCount();
                            totalccCount[0] += childCount;
                            //Log.e("Date: ", childCount);
                            totalContacts.setText(String.valueOf(totalccCount[0]));

                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    keyRef.addListenerForSingleValueEvent(eventListener);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



    }
    private void getUserSymptoms(String uid) {

        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("users/"+uid+"/userSymptoms");

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> symptomsList = new ArrayList<>();

                for (int i = 0; i <= dataSnapshot.getChildrenCount()-1;i++){
                    String comment = String.valueOf(dataSnapshot.child(String.valueOf(i)).getValue());
                    symptomsList.add(comment);
                }

                String userSymptoms = symptomsList.toString().replace(", ", "\n").replace("[", "").replace("]", "");

                userSymptomsText.setText(userSymptoms);
                //TODO::ADD SEPARATE TEXTVIEW IN BOTTOM

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    private void getDbChartData(String uid,String date) {


        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("ClosedContact/"+uid);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String date = String.valueOf(postSnapshot.child("date").getValue());
                    Log.e("Date: ", date);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void showBarGraphData(String uid){
        String[] dates = new String[14];

        BarChart mChart = findViewById(R.id.homeBarGraph);
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
        xaxis.setCenterAxisLabels(false);

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();//bar number data

        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query ref = database.getReference("ClosedContact/"+uid).limitToLast(14);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey();//NewCode Start
                final int[] finalJ = {0};
                final boolean[] hasExposureData = {false};
                for (int j = 0;j <= exposureDates.size()-1;j++) {

                    DatabaseReference keyRef = database.getReference("ClosedContact/"+ uid + "/"+exposureDates.get(j));
                    String name = dataSnapshot.child("name").getValue(String.class);


                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long ccListCount = dataSnapshot.child("ccList").getChildrenCount();
                            String dateOfExposure = String.valueOf(dataSnapshot.child("date").getValue());

                            boolean childExist = !dateOfExposure.equals("null");
                            if (childExist){
                                hasExposureData[0] = true;

                                BarEntry entry = new BarEntry(finalJ[0], ccListCount);
                                valueSet1.add(entry);

                                dates[finalJ[0]] = dateOfExposure;
                                xaxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                                finalJ[0]++;

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    keyRef.addListenerForSingleValueEvent(eventListener);

                        YAxis yAxisLeft = mChart.getAxisLeft();
                        yAxisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisLeft.setAxisMaximum(10 + .5f);
                        yAxisLeft.setAxisMinimum(0 - .5f);
                        yAxisLeft.setGranularity(1.0f);//setting yAxis values to int
                        yAxisLeft.setGranularityEnabled(true);
                        mChart.setVisibleYRangeMaximum(150, YAxis.AxisDependency.LEFT);
                        yAxisLeft.setDrawAxisLine(false);



                }//NEW Code End



//                int i = 0;
//                for (int j = 0;j <= exposureDates.size()-1;j++) {
//                    Iterable<DataSnapshot> ds = dataSnapshot.child("2021-11-14").getChildren();//TODO use default fetch
//
//                    for (DataSnapshot postSnapshot : ds) {
//
//
//                        long ccListCount = postSnapshot.child("ccList").getChildrenCount();
//                        String dateOfExposure = String.valueOf(postSnapshot.child("date").getValue());
//
//                        i++;
//                        Toast.makeText(getApplicationContext(), ""+postSnapshot, Toast.LENGTH_LONG).show();
////
////                        BarEntry entry = new BarEntry(j, ccListCount);
////                        valueSet1.add(entry);
////
////                        dates[j] = dateOfExposure;
////                        xaxis.setValueFormatter(new IndexAxisValueFormatter(dates));
//                    }
//                }

                /*
                 * Boundary
                 */

//                for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
//                    i += 1;
//
//                    Toast.makeText(getApplicationContext(), "Loob ng loop "+i, Toast.LENGTH_SHORT).show();
//                    Iterable<DataSnapshot> ds = dataSnapshot.child(formatter.format(date)).getChildren();
//
//                    for (DataSnapshot postSnapshot : ds) {
//                        Toast.makeText(getApplicationContext(), ""+postSnapshot, Toast.LENGTH_SHORT).show();
//                        long ccListCount = postSnapshot.child("ccList").getChildrenCount();
//                        String dateOfExposure = String.valueOf(postSnapshot.child("date").getValue());
//
//                        BarEntry entry = new BarEntry(i, ccListCount);
//                        valueSet1.add(entry);
//
//                        dates[i] = dateOfExposure;
//                        xaxis.setValueFormatter(new IndexAxisValueFormatter(dates));
//
//                    }
//                }
//                int i = 0;//OLD CODE
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Toast.makeText(getApplicationContext(), ""+postSnapshot, Toast.LENGTH_LONG).show();
//                    i += 1;
//                    long ccListCount = postSnapshot.child("ccList").getChildrenCount();
//                    String date = String.valueOf(postSnapshot.child("date").getValue());
//
//                    BarEntry entry = new BarEntry(i, ccListCount);
//                    valueSet1.add(entry);
//
//                    dates[i] = date;
//                    xaxis.setValueFormatter(new IndexAxisValueFormatter(dates));
//
//                    YAxis yAxisLeft = mChart.getAxisLeft();
//                    yAxisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
//                    yAxisLeft.setAxisMaximum(10 + .5f);
//                    yAxisLeft.setAxisMinimum(0 - .5f);
//                    //yAxisLeft.setAxisMinValue(0f);//setting yAxis start value to 0
//                    yAxisLeft.setGranularity(1.0f);//setting yAxis values to int
//                    yAxisLeft.setGranularityEnabled(true);
//                    mChart.setVisibleYRangeMaximum(150, YAxis.AxisDependency.LEFT);
////        yAxisLeft.setDrawGridLines(false);
//                    yAxisLeft.setDrawAxisLine(false);
////        yAxisLeft.setEnabled(false);
//
//
//
//                }//OLD CODE END
                final Handler handler = new Handler(Looper.getMainLooper());//delay the code a for 1 second
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChart.getAxisRight().setEnabled(false);
                        mChart.setVisibleXRangeMaximum(2);
                        mChart.setMaxVisibleValueCount(2);

                        Legend legend = mChart.getLegend();
                        legend.setEnabled(false);

                        List<IBarDataSet> dataSets = new ArrayList<>();
                        BarDataSet barDataSet = new BarDataSet(valueSet1, " ");
                        barDataSet.setColor(Color.BLUE);
                        barDataSet.setDrawValues(false);
                        dataSets.add(barDataSet);

                        BarData data = new BarData(dataSets);

                        mChart.setData(data);
                        mChart.invalidate();
                    }
                }, 1000);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }


}
