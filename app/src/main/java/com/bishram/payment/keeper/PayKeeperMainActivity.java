package com.bishram.payment.keeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean hasUser = checkUser();

        if (!hasUser) {
            // TODO: Goto login activity
            showToast("No user is logged into the app.");
        } else {
            showToast("User is already logged into the app.");
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
