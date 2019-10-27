package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.bishram.payment.keeper.Constants.FIREBASE_USER_OWNER_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USER_RENTER_PATH;

public class PayKeeperMainActivity extends AppCompatActivity {

    private ProgressBar progressBarFetchingData;

    // Declare an instance of FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Declare an instance of root path of Realtime Database
    private DatabaseReference referenceRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pay_keeper);

        // Initialize xml view objects
        initializeViews();

        // Initialize firebase objects
        initializeFirebase();

        if (!checkUser()) {
            // No logged user if found
            // In case Goto login activity
            startActivity(new Intent(PayKeeperMainActivity.this, PayKeeperLoginActivity.class));
            finish();
        } else {
            // Logged user is found
            getUserDetails();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // All initialization related to UI views will go here
    private void initializeViews() {
        progressBarFetchingData = findViewById(R.id.pb_main_fetching_user_data);
    }

    // All initialization related to firebase will go here
    private void initializeFirebase() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Path Reference to the root of the Realtime Database
        referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    private boolean checkUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // User already logged in
        // User has not logged in yet
        return currentUser != null;
    }

    private void getUserDetails() {
        // Read owner's path first
        readFirebaseOwnerDetails();
    }

    // Read firebase database owner path
    private void readFirebaseOwnerDetails() {
        // Path reference to the User "Owner"
        DatabaseReference referenceUserOwner = referenceRoot.child(FIREBASE_USER_OWNER_PATH);

        progressBarFetchingData.setVisibility(View.VISIBLE);

        referenceUserOwner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    progressBarFetchingData.setVisibility(View.GONE);

                    startActivity(new Intent(PayKeeperMainActivity.this, UserRegistrationActivity.class));
                    finish();
                } else {
                    // No owner was found
                    // So, read renter's path
                    startActivity(new Intent(PayKeeperMainActivity.this, UserRegistrationActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Read firebase database renter path
    private void readFirebaseRenterDetails() {
        // Path reference to the User "Renter"
        DatabaseReference referenceUserRenter = referenceRoot.child(FIREBASE_USER_RENTER_PATH);

        referenceUserRenter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showToast(String stringMessage) {
        Toast.makeText(this, stringMessage, Toast.LENGTH_SHORT).show();
    }
}
