package com.ucc.csbsafety;

import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RSSPullService extends JobService {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();// Get a reference to your user
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // runs on the main thread, so this Toast will appear
        dispUserName(database);
        // perform work here, i.e. network calls asynchronously

        // returning false means the work has been done, return true if the job is being run asynchronously
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        dispUserName(database);
        Toast.makeText(getApplicationContext(), "Ended", Toast.LENGTH_SHORT).show();
        return false;
    }






//
//import android.app.IntentService;
//import android.content.Intent;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//public class RSSPullService extends IntentService {
//    /**
//     * @param name
//     * @deprecated
//     */
//    public RSSPullService() {
//
//    }
//
//    @Override
//    public void onHandleIntent(Intent workIntent) {
//        // Gets data from the incoming Intent
//        String dataString = workIntent.getDataString();
//        // Do work here, based on the contents of dataString
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();// Get a reference to your user
//
//        dispUserName(database);
//
//    }
    private void dispUserName(FirebaseDatabase database) {
        FirebaseAuth user = FirebaseAuth.getInstance();// Initialize Firebase Auth


        String id = user.getUid();
        //displaying Fullname after getting it to realtime db
        DatabaseReference ref = database.getReference("users/"+id);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                assert profile != null;
                Toast.makeText(getApplicationContext(), ""+profile.getFullname(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
