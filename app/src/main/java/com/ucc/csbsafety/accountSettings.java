package com.ucc.csbsafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucc.csbsafety.adapters.settingsAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class accountSettings extends AppCompatActivity {
    BottomNavigationView botNav;
    settingsAdapter adapter;
    RecyclerView recyclerView;
    List<String> titles;
    TextView txtFullname,txtUid,txtEmail,txtUserStatus;
    CircleImageView profilePic;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        botNav = findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.settingRecycler);
        txtFullname = findViewById(R.id.accUsername);
        txtUid = findViewById(R.id.accUid);
        txtEmail = findViewById(R.id.accEmail);
        txtUserStatus = findViewById(R.id.userStatus);
        profilePic = findViewById(R.id.profile_image);

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        showUserInfo(user,database);

        titles = new ArrayList<>();//creating new list
        //adding settings items
        titles.add("Account");
        titles.add("Contact Tracing List");
        titles.add("Terms of Use");
        titles.add("About us");
        titles.add("Privacy Policy");
        titles.add("Log Out");

        setupRecyclerAdapter();

        botNav.setSelectedItemId(R.id.account);//Set Home Selected
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
                        accountSettings.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void setupRecyclerAdapter() {
        adapter = new settingsAdapter(this,titles);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

    }
    private void showUserInfo(FirebaseUser user,FirebaseDatabase database){
        String id = user.getUid();
        //displaying Fullname after getting it to realtime db
        DatabaseReference ref = database.getReference("users/"+id);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                txtFullname.setText(profile.getFullname());
                String img = profile.getImage();
                if (img != null) {
                    try {
                        Glide.with(accountSettings.this)
                                .load(img)
                                .into(profilePic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        txtUid.setText(id);
        txtEmail.setText(user.getEmail());

    }
}