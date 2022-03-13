package com.ucc.csbsafety;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.firebase.auth.FirebaseAuth;

import static android.service.controls.ControlsProviderService.TAG;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText txtBoxEmail,txtBoxPassword;
    Button btnLogin;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtBoxEmail = findViewById(R.id.loginEmail);
        txtBoxPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> validate());
        checkForUpdates();

        if (getIntent() != null){
            catchIntent();
        }


    }
    @Override
    public void onBackPressed() {// ask user when he pressed back button
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Login.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void checkForUpdates() {
        //UPDATING THE APP
        AppUpdater appUpdater = new AppUpdater(this);

        appUpdater.setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/janrebgasco/csb-update-log.json/main/update-changelog.json")
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available of this app")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update")
                .setIcon(R.drawable.logo) // Notification icon
                .setCancelable(false)
                .setButtonDoNotShowAgain(null); // Dialog could not be dismissable;

        appUpdater.start();
    }

    private void catchIntent() {
        Intent i = getIntent();
        String email = i.getStringExtra("email");
        String password = i.getStringExtra("password");
        txtBoxEmail.setText(email);
        txtBoxPassword.setText(password);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void validate() {
        if (txtBoxEmail.length() < 1) {
            txtBoxEmail.setError("Email is required");
            txtBoxEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(txtBoxEmail.getText().toString()).matches()) {
            txtBoxEmail.setError("Error invalid Email");
            txtBoxEmail.requestFocus();
        }
        else if (txtBoxPassword.length() < 1) {
            txtBoxPassword.setError("Password confirmation is required");
            txtBoxPassword.requestFocus();
        }
        else if (txtBoxPassword.length() < 8) {
            txtBoxPassword.setError("Invalid password");
            txtBoxPassword.requestFocus();
        }
        else{
            String email = txtBoxEmail.getText().toString();
            String password = txtBoxPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Login.this, task -> {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified())
                            {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                finish();
                                startService(new Intent(this, BackgroundService.class));
                                Intent i = new Intent(Login.this, HomeScreen.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                Toast.makeText(Login.this, "Please verify your email first.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Incorrect email or password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void showSignUp(View view) {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
        finish();
    }

    public void gotoForgotPass(View view) {
        Intent i = new Intent(this, forgotPassword.class);
        startActivity(i);
        finish();
    }
}