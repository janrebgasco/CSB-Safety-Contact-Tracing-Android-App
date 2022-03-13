package com.ucc.csbsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ucc.csbsafety.utility.PreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        boolean isFirstTimeUser = PreferenceUtils.getFTU(this);
        String lastUserStatusCheck = PreferenceUtils.getUserStatusCheckDate(this);

        //Creates a splash screen
        new Thread(() -> {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally {
                finish();
            }

            Intent i;
            if (user != null){
                if (lastUserStatusCheck != null){
                    boolean hasUserChecked = lastUserStatusCheck.equals(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()));
                    startService(new Intent(this, BackgroundService.class));
                    if (hasUserChecked){
                        i = new Intent(this, HomeScreen.class);
                    }else{
                        i = new Intent(this, DailyUserStatusCheck.class);
                    }
                }
                else{
                    i = new Intent(this, DailyUserStatusCheck.class);
                }

            }
            else if (!isFirstTimeUser){
                i = new Intent(this, Login.class);
            }
            else {
                i = new Intent(this, NewUserSlider.class);
            }
            startActivity(i);
        }).start();
    }


}