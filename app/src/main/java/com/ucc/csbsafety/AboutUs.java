package com.ucc.csbsafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.ucc.csbsafety.adapters.preventionAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutUs extends AppCompatActivity {
    aboutUsAdapter adapter;
    RecyclerView aboutUsRecycler;
    List<String> titles;
    List<Integer> imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        aboutUsRecycler = findViewById(R.id.aboutUsRecycler);

        titles = new ArrayList<>();
        imgs = new ArrayList<>();

        addItemsToList();
        setupAdapter();
    }

    private void addItemsToList() {
        imgs.add(R.drawable.eyang);
        titles.add("Andrea Austero\nSystem Analyst/Researcher");
        imgs.add(R.drawable.cimatu);
        titles.add("Christian F. Cimatu\nSystem Analyst/Researcher");
        imgs.add(R.drawable.toph);
        titles.add("Christoper D. Fernando\nQA Engineer/Researcher");
        imgs.add(R.drawable.rebo);
        titles.add("Janreb S. Gasco\nAndroid Developer/Programmer");
        imgs.add(R.drawable.jeck2);
        titles.add("Jeck Allan Guelas\nUI/UX Designer, C++ Programmer");
        imgs.add(R.drawable.tiff);
        titles.add("Tiffany Jade F. Jacinto\nUI/UX Designer, Researcher");
        imgs.add(R.drawable.ge);
        titles.add("Gerald C. Parico\nTeam Leader/ Project Manager, System Analyst");
        imgs.add(R.drawable.bon);
        titles.add("Bonzi L. Sarmiento\nWeb Developer/Programmer");

    }

    private void setupAdapter() {
        adapter = new aboutUsAdapter(this,titles,imgs);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        aboutUsRecycler.setLayoutManager(gridLayoutManager);
        aboutUsRecycler.setAdapter(adapter);
        aboutUsRecycler.setNestedScrollingEnabled(false);
    }

    public void aboutUsBack(View view) {
        super.onBackPressed();
    }
}