package com.ucc.csbsafety;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btnSignup;
    EditText txtBoxEmail,txtBoxPassword,txtBoxFName,txtBoxConfirmPass,txtBoxLName,txtStudentNum,txtOtherUserType;
    private DatabaseReference mDatabase;
    CheckBox chkAgree;
    Spinner ddownUserType;
    ConstraintLayout rootView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        btnSignup = findViewById(R.id.btnSignUp);
        txtBoxEmail = findViewById(R.id.txtBoxEmail);
        txtBoxPassword = findViewById(R.id.txtBoxPassword);
        txtBoxFName = findViewById(R.id.signupFname);
        txtBoxLName = findViewById(R.id.signupLname);
        txtBoxConfirmPass = findViewById(R.id.txtBoxConfirmPass);
        chkAgree = findViewById(R.id.chkAgree);
        ddownUserType = findViewById(R.id.spinnerUserType);
        txtStudentNum = findViewById(R.id.txtStudentNum);
        txtOtherUserType = findViewById(R.id.txtOtherUserType);
        rootView = findViewById(R.id.signupRootView);
        frameLayout = findViewById(R.id.signUpFrameLayout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //Signup button onclick
        btnSignup.setOnClickListener(view -> {
            String fname = txtBoxFName.getText().toString();
            String lname = txtBoxLName.getText().toString();
            String email = txtBoxEmail.getText().toString();
            String password = txtBoxPassword.getText().toString();

            userValidation(fname,lname,password,email);
        });
        ddownUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 1){
                    txtStudentNum.setVisibility(View.VISIBLE);
                    txtOtherUserType.setVisibility(View.GONE);
                }
                else if (i == 5){
                    txtOtherUserType.setVisibility(View.VISIBLE);
                    txtStudentNum.setVisibility(View.GONE);
                }
                else{
                    txtStudentNum.setVisibility(View.GONE);
                    txtOtherUserType.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        KeyboardVisibilityEvent.setEventListener(
                SignUp.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if(isOpen){
                            frameLayout.setVisibility(View.GONE);
                        }else{
                            frameLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });


    }

    @Override
    public void onBackPressed() {// ask user when he pressed back button
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SignUp.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void signUp(String email,String password,String fname,String lname,String studentNum,String UserType){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "Email verification has been sent to your email");
                                        Toast.makeText(SignUp.this, "Verification link has been sent to you email.", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(this,Login.class);
                                        i.putExtra("email",email);
                                        i.putExtra("password",password);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(SignUp.this, "Failed to send verification email.\nPlease check you internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        String uid = mAuth.getCurrentUser().getUid();//getting user id
                        User user = new User(fname, lname, email, "negative",studentNum,UserType);
                        mDatabase.child("users").child(uid).setValue(user);
                        // show log message when succeeded
                        Log.d(TAG, "createUserWithEmail:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                });
    }
    public void userValidation(String fname,String lname,String password,String email) {
        if (txtBoxFName.getText().toString().isEmpty()) {
            txtBoxFName.setError("Please enter your first name ");
            txtBoxFName.requestFocus();
            return;
        }
        if (txtBoxLName.getText().toString().isEmpty()) {
            txtBoxLName.setError("Please enter your last name ");
            txtBoxLName.requestFocus();
            return;
        }
        String emailInput = txtBoxEmail.getText().toString().trim();
        if (emailInput.length() < 1) {
            txtBoxEmail.setError("Email is required");
            txtBoxEmail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            txtBoxEmail.setError("Error invalid Email");
            txtBoxEmail.requestFocus();
            return;
        }
        if (txtBoxConfirmPass.length() < 1) {
            txtBoxConfirmPass.setError("Password is required");
            txtBoxConfirmPass.requestFocus();
            return;
        }
        else if (txtBoxConfirmPass.length() < 8) {
            txtBoxConfirmPass.setError("Error must contain at least 8 characters");
            txtBoxConfirmPass.requestFocus();
            return;
        }
        if (txtBoxPassword.length() < 1) {
            txtBoxPassword.setError("Password confirmation is required");
            txtBoxPassword.requestFocus();
            return;
        }
        if (!txtBoxPassword.getText().toString().equals(txtBoxConfirmPass.getText().toString())) {
            txtBoxPassword.setError("Password does not match");
            txtBoxPassword.requestFocus();
        }
        if (ddownUserType.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
        }
        if(txtStudentNum.getVisibility() == View.VISIBLE && txtStudentNum.length() < 1 || txtStudentNum.length() < 1){
            Toast.makeText(this, "Please enter 8 digit student number", Toast.LENGTH_SHORT).show();
        }
        if (txtOtherUserType.getVisibility() == View.VISIBLE && txtOtherUserType.length()<1){
            Toast.makeText(this, "Please specify other user type", Toast.LENGTH_SHORT).show();
        }
        else if (!chkAgree.isChecked()) {
            Toast.makeText(SignUp.this, "Agree to the terms and conditions to continue.", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        boolean emailDontExist = task.getResult().getSignInMethods().isEmpty();
                        if (!emailDontExist) {
                            txtBoxEmail.setError("Email is already taken");
                            txtBoxEmail.requestFocus();
                        }
                        else {
                            String userType;
                                if (ddownUserType.getSelectedItemPosition() == 5){
                                    userType = txtOtherUserType.getText().toString();
                                }
                                else{
                                    userType = ddownUserType.getSelectedItem().toString();
                                }

                            String studentNum = txtStudentNum.getText().toString()+"-M";
                            signUp(email,password,fname,lname,studentNum,userType);
                        }
                    });
        }
    }

    public void showLogin(View view) {
        Intent i = new Intent(this,Login.class);
        startActivity(i);
        finish();
    }

    public void gotolink(View view) {
        goTourl("https://www.websitepolicies.com/policies/view/7g18lt1z");
    }
    private void goTourl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}