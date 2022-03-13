package com.ucc.csbsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class statReport extends AppCompatActivity {
    BottomNavigationView botNav;
    // creating a variable
    // for our graph view.
    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_report);
        botNav = findViewById(R.id.bottom_navigation);


        // on below line we are initializing our graph view.
        graphView = findViewById(R.id.idGraphView);

        // on below line we are adding data to our graph view.
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                // on below line we are adding
                // each point on our x and y axis.
                new DataPoint(0, 16000),
                new DataPoint(1, 17536),
                new DataPoint(2, 24241),
                new DataPoint(3, 23512),
                new DataPoint(4, 22523),
                new DataPoint(5, 16124),
                new DataPoint(6, 12535),
                new DataPoint(7, 8984),
                new DataPoint(8, 4027)
        });

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        graphView.setTitle("Live Covid 19 Cases in Philippines");

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(24);

        // on below line we are adding
        // data series to our graph view.
        graphView.addSeries(series);

        botNav.setSelectedItemId(R.id.report);//Set Home Selected
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

    public void gotoUserCheck(View view) {
        Intent i = new Intent(this,notification.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {// ask user when he pressed back button
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        statReport.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}