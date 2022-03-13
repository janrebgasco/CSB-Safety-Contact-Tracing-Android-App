package com.ucc.csbsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.ucc.csbsafety.utility.PreferenceUtils;

public class getStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

    }

    public void startNow(View view) {
        PreferenceUtils.isFirstTimeUser(false,this);
        finish();
        Intent i = new Intent(this,Login.class);
        startActivity(i);
    }

}