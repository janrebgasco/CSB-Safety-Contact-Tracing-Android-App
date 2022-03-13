package com.ucc.csbsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NotifMoreInfo extends AppCompatActivity {
    TextView txtTitle, txtMoreInfo, txtDateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_more_info);
        txtTitle = findViewById(R.id.textView53);
        txtMoreInfo = findViewById(R.id.textView13);
        txtDateAndTime = findViewById(R.id.textView54);

        catchIntent();

    }
    private void catchIntent() {
        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String moreInfo = i.getStringExtra("moreInfo");
        String dateAndTime = i.getStringExtra("dateAndTime");

        txtTitle.setText(title);
        txtMoreInfo.setText(moreInfo);
        txtDateAndTime.setText(dateAndTime);
    }
    public void moreInfoBack(View view) {
        super.onBackPressed();
    }
}