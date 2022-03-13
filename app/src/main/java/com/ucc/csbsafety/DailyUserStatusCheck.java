package com.ucc.csbsafety;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucc.csbsafety.utility.PreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyUserStatusCheck extends AppCompatActivity {
    //initializing variables
    RecyclerView recyclerView;
    public static List<String> titles;
    List<Integer> images;
    Adapter adapter;
    RecyclerView.LayoutManager manager;
    List<String> selectedSymptoms = new ArrayList<>();
    Button symptomsSubmit;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_user_status_check);
        recyclerView = findViewById(R.id.recyclerview);
        symptomsSubmit = findViewById(R.id.symptomsSubmit);

        addItemsToCategory();
        setupRecyclerAdapter();

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();//get firebase realtime db instance
        FirebaseUser user = mAuth.getCurrentUser();//get logged in user in firebase auth
        assert user != null;
        String uid = user.getUid();

        symptomsSubmit.setOnClickListener(view -> {

            new AlertDialog.Builder(DailyUserStatusCheck.this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure the you want to proceed?")
                    .setIcon(R.drawable.question_mark_icon)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

                            mDatabase.child("users").child(uid).child("userSymptoms").setValue(selectedSymptoms);
                            PreferenceUtils.saveUserStatusCheckDate(date,DailyUserStatusCheck.this);

                            Intent i = new Intent(getApplicationContext(),HomeScreen.class);
                            startActivity(i);
                            finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();


        });


    }

    private void addItemsToCategory() {//adds the title and image to category
        titles = new ArrayList<>();
        images = new ArrayList<>();
        titles.add("Cough");
        images.add(R.drawable.group_922);
        titles.add("Fever");
        images.add(R.drawable.group_936);
        titles.add("Nasal Congestion");
        images.add(R.drawable.group_945);
        titles.add("Runny Nose");
        images.add(R.drawable.group_988);
        titles.add("Sore Throat");
        images.add(R.drawable.group_1002);
        titles.add("Fatigue");
        images.add(R.drawable.group_1013);
        titles.add("Tired");
        images.add(R.drawable.group_954);
        titles.add("Difficulty Breathing");
        images.add(R.drawable.group_963);
        titles.add("Headache");
        images.add(R.drawable.group_976);
    }

    private void setupRecyclerAdapter() {//sets up the layout if recyclerView as  well as the adapter for Categories
        adapter = new Adapter(this,titles,images,new Adapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(String selectedItem) {
                selectedSymptoms.add(selectedItem);

                if (selectedSymptoms.size() > 0){
                    symptomsSubmit.setText("Submit");
                }else{
                    symptomsSubmit.setText("Skip");
                }
            }

            @Override
            public void onItemUncheck(String selectedItem) {
                selectedSymptoms.remove(selectedItem);

                if (selectedSymptoms.size() > 0){
                    symptomsSubmit.setText("Submit");
                }else{
                    symptomsSubmit.setText("Skip");
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}