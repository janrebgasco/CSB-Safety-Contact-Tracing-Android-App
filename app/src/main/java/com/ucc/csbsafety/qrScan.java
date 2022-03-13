package com.ucc.csbsafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class qrScan extends AppCompatActivity {
    BottomNavigationView botNav;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnScan;
    String uid,dateNum,date,Fname,Lname,Image,Status,StudentNum,UserType;
    FirebaseAuth user;
    private DatabaseReference mDatabase;

    List<String> uids = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<String> status = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnScan = findViewById(R.id.btnScan);
        botNav = findViewById(R.id.bottom_navigation);

        user = FirebaseAuth.getInstance();// Initialize Firebase Auth
        mDatabase = FirebaseDatabase.getInstance().getReference();//Realtime Database
        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        getUserInfo(FUser,database);

        uid = user.getUid();

        dateNum = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());//number format of date
        date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());//

        mDatabase.child("ClosedContact").child(uid).child(dateNum).child("closedContactCount").setValue(0);
        mDatabase.child("ClosedContact").child(uid).child(dateNum).child("date").setValue(date);


        botNav.setSelectedItemId(R.id.scan);//Set Home Selected
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
                    startActivity(new Intent(getApplicationContext()
                            ,notification.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.scan:
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
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        qrScan.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1080, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(qrScan.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(qrScan.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            String scanText = barcodes.valueAt(0).displayValue;

                            String qrText = scanText;//room-establishment-department
                            String[] separated = qrText.split("-");//room,establishment,department
                            if (separated.length == 4){
                                String establishment = separated[1];
                                String dept = separated[2];
                                String action = separated[3];
                                txtBarcodeValue.setText(establishment +"," + dept);

                                if (!checkInternetConn()){
                                    Toast.makeText(qrScan.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                                }
                                else if(action.equals("leave") || action.equals("LEAVE")){
                                    stopDetections();
                                    removeUserToList(establishment,dept);
                                }
                                else{
                                    stopDetections();
                                    getperonInsideRoom(establishment,dept,uid,dateNum);
                                }
                            }
                            else if(separated.length == 2){
                                stopDetections();
                                String establishment = separated[1];

                                insertUser(establishment,uid);
                                txtBarcodeValue.setText("Establishment : " + establishment);
                                Intent intent = new Intent(qrScan.this,HomeScreen.class);
                                startActivity(intent);
                                finish();
                            }else{
                                txtBarcodeValue.setText("Invalid QR Code");
                            }

                        }

                        private void stopDetections() {
                            barcodeDetector.release();
                            cameraSource.stop();
                            cameraSource.release();
                        }

                    });


                }
            }
        });
    }

    private void removeUserToList(String establishment,String dept) {
        mDatabase.child("room").child(establishment).child(dept).child(uid).removeValue();
        Toast.makeText(getApplicationContext(), "You have left the department", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),scanSuccess.class);
        startActivity(intent);
    }

    private boolean checkInternetConn() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void getperonInsideRoom(String establishment, String department,String uid, String date) {
        Toast.makeText(qrScan.this, "Please wait....\nGetting list of person inside", Toast.LENGTH_SHORT).show();

        String currentime = getTime();
        String fullDate = new SimpleDateFormat("MMM dd, y", Locale.getDefault()).format(new Date());

        MyInfo myInfo = new MyInfo(uid,fullDate,currentime,department,"negative");//TODO :: replace negative status with db data
        mDatabase.child("room").child(establishment).child(department).child(uid).setValue(myInfo);//insert user to the room

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
                    time.add(String.valueOf(children.child("time").getValue()));
                    status.add(String.valueOf(children.child("status").getValue()));

                    insertUserContacts(uid, date, department, establishment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(qrScan.this, "An error occurred\n "+error, Toast.LENGTH_SHORT).show();
            }
        };
        ref.addListenerForSingleValueEvent(listener);

    }
    private void insertUserContacts(String myUid,String date,String department,String establishment) {
        String currentime = getTime();
        String fullDate = new SimpleDateFormat("MMM dd, y", Locale.getDefault()).format(new Date());

        for (int i = 0; i < uids.size();i++){
            if (!uid.equals(uids.get(i))){//if my uid is not equal to contact uid
                String UID = uids.get(i);
                String Time = time.get(i);
                String Status = status.get(i);

                InsideList person = new InsideList(UID,fullDate,currentime,department,Status);
                MyInfo myInfo = new MyInfo(myUid,fullDate,currentime,department,"negative");//TODO :: replace negative status with db data

                mDatabase.child("ClosedContact").child(myUid).child(date).child("ccList").child(UID).setValue(person);//insert close contact to user
                mDatabase.child("ClosedContact").child(UID).child(date).child("ccList").child(myUid).setValue(myInfo);//insert close contact to other users
                mDatabase.child("room").child(establishment).child(department).child(myUid).setValue(myInfo);//insert user to the room

                //Toast.makeText(this, "Inserted "+UID +" to close contact.", Toast.LENGTH_SHORT).show();
            }

        }
        Toast.makeText(getApplicationContext(), "Person inside has been added to you\n Close Contact", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),scanSuccess.class);
        startActivity(intent);
    }
    private String getTime() {
        String delegate = "hh:mm aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public void gotoShowQr(View view) {
        Intent i = new Intent(this,showUserQr.class);
        startActivity(i);
    }
    private void insertUser(String establishment,String uid) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("establishment/"+ establishment);

        UserData user = new UserData(Lname, Fname,Status,Image,StudentNum,UserType,uid);

        mDatabase.child("userData").setValue(user);
        Toast.makeText(getApplicationContext(), "QR code has been scanned", Toast.LENGTH_SHORT).show();
    }
    private void getUserInfo(FirebaseUser user,FirebaseDatabase database) {
        String id = user.getUid();
        //displaying Fullname after getting it to realtime db
        DatabaseReference ref = database.getReference("users/" + id);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile2 profile = dataSnapshot.getValue(Profile2.class);

                Fname = profile.getFName();
                Lname = profile.getLName();
                Image = profile.getImage();
                Status = profile.getStatus();
                StudentNum = profile.getStudentNum();
                UserType = profile.getUserType();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}