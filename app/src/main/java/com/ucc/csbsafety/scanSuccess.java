package com.ucc.csbsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class scanSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_success);
    }

    public void gotoHome(View view) {
        Intent i = new Intent(this,HomeScreen.class);
        startActivity(i);
    }
}