package com.ucc.csbsafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ucc.csbsafety.adapters.preventionAdapter;
import com.ucc.csbsafety.adapters.symptomAdapter;

import java.util.ArrayList;
import java.util.List;

public class symptomsInfo extends AppCompatActivity {
    BottomNavigationView botNav;
    symptomAdapter adapter;
    preventionAdapter adapter2;
    RecyclerView recyclerView,preventionRecycler;
    List<String> titles;
    List<Integer> images;
    List<String> preventionTitles;
    List<Integer> preventionImgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_info);
        botNav = findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.symptomRecycler);
        preventionRecycler = findViewById(R.id.preventionRecycler);

        recyclerView.setFocusable(false);
        preventionRecycler.setFocusable(false);

        titles = new ArrayList<>();
        images = new ArrayList<>();
        preventionTitles = new ArrayList<>();
        preventionImgs = new ArrayList<>();

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

        preventionTitles.add("Wear a mask");
        preventionImgs.add(R.drawable.group_783);
        preventionTitles.add("Clean and Disinfect");
        preventionImgs.add(R.drawable.group_825);
        preventionTitles.add("Avoid touching \nyour face");
        preventionImgs.add(R.drawable.group_796);
        preventionTitles.add("Wash your hands \nfrequently");
        preventionImgs.add(R.drawable.group_786);
        preventionTitles.add("Keep distance \nfrom others");
        preventionImgs.add(R.drawable.group_815);
        preventionTitles.add("Stay at home");
        preventionImgs.add(R.drawable.group_775);






        setupRecyclerAdapter();

        botNav.setSelectedItemId(R.id.info);//Set Home Selected
        //perform item seleted listener
        botNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    startActivity(new Intent(getApplicationContext()
                            , HomeScreen.class));
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
    private void setupRecyclerAdapter() {//sets up the layout if recyclerView as  well as the adapter for Categories
        adapter = new symptomAdapter(this,titles,images);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter2 = new preventionAdapter(this,preventionTitles,preventionImgs);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        preventionRecycler.setLayoutManager(gridLayoutManager2);
        preventionRecycler.setAdapter(adapter2);

    }
    @Override
    public void onBackPressed() {// ask user when he pressed back button
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        symptomsInfo.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}