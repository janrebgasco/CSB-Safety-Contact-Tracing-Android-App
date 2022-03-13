package com.ucc.csbsafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class forgotPassword extends AppCompatActivity {
    EditText mEmail;
    FirebaseAuth mFirebaseAuth;
    int count = 20;
    Button mButton;
    Boolean isEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mEmail = findViewById(R.id.txtBoxForgotEmail);
        mButton = findViewById(R.id.btnForgotEmail);

        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    public void recoveryBack(View view) {
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    public void recoverBtn(View view) {
        final String email = mEmail.getText().toString();
        if (!isEnabled){
            Toast.makeText(this, "Try again in "+count+"s", Toast.LENGTH_SHORT).show();
        }
        else if (email.equals("")){
            mEmail.setError("Email is required");
            mEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Invalid email");
            mEmail.requestFocus();
        }
        else {
            mFirebaseAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        boolean emailDontExist = task.getResult().getSignInMethods().isEmpty();
                        if (emailDontExist) {
                            mEmail.setError("Unregistered Email");
                            mEmail.requestFocus();
                        }
                        else {
                            //insertToFirebase(email);
                            mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Password reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                                    timer();
                                    mButton.setText("Resend Link");
                                    isEnabled = false;
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
        }
    }

    private void timer() {
        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                count--;
                //Toast.makeText(ForgotPassword.this, ""+count, Toast.LENGTH_SHORT).show();
            }

            public void onFinish() {
                isEnabled = true;
                mButton.setEnabled(true);
            }
        }.start();
    }


}