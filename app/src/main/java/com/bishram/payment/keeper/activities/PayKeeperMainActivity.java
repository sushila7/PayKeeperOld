package com.bishram.payment.keeper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PayKeeperMainActivity extends AppCompatActivity {

    // Declare an instance of FirebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pay_keeper);

        //Initialize firebase objects
        initializeFirebase();

        if (!checkUser()) {
            // No logged user if found
            // In case Goto login activity
            startActivity(new Intent(PayKeeperMainActivity.this, PayKeeperLoginActivity.class));
            finish();
        } else {
            // Logged user is found
            showToast("User has logged in");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean hasUser = checkUser();

        if (!hasUser) {
            // Goto login activity
            startActivity(new Intent(PayKeeperMainActivity.this, PayKeeperLoginActivity.class));
            finish();
        } else {
            showToast("Already logged IN");
        }
    }

    // All initialization related to firebase will go here
    private void initializeFirebase() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private boolean checkUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // User already logged in
        // User has not logged in yet
        return currentUser != null;
    }

    private void showToast(String stringMessage) {
        Toast.makeText(this, stringMessage, Toast.LENGTH_SHORT).show();
    }
}
