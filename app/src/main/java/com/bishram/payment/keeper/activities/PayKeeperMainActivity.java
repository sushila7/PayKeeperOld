package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.OwnersOfHouse;
import com.bishram.payment.keeper.models.RentersOfHouse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.bishram.payment.keeper.Constants.FIREBASE_USER_OWNER_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USER_RENTER_PATH;
import static com.bishram.payment.keeper.Constants.KEY_USER;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class PayKeeperMainActivity extends AppCompatActivity implements View.OnClickListener{

    // Declare view instances below
    private Button buttonOwnersRenters;
    private Button buttonAboutApp;

    private ProgressBar progressBarFetchingData;

    private TextView textViewDisplayName;
    private TextView textViewORMobile;
    private TextView textViewUserCategory;

    private FirebaseUser currentUser;

    // Declare an instance of root path of Realtime Database
    private DatabaseReference referenceRoot;

    private boolean userFound;

    private String thisUserMobileNumber;
    private String displayName;
    private String displayMobile;
    private String userCategory;
    private String toastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pay_keeper);

        // Initialize xml view objects
        initializeViews();

        // Initialize firebase objects
        initializeFirebase();

        setButtonClickListeners();

        if (currentUser != null) {
            thisUserMobileNumber = currentUser.getPhoneNumber();
            userFound = false;
            readFirebaseOwnerDetails();
        }
    }

    // All initialization related to UI views will go here =========================================
    private void initializeViews() {
        buttonOwnersRenters = findViewById(R.id.button_main_owners_renters);
        buttonAboutApp = findViewById(R.id.button_main_about_app);

        progressBarFetchingData = findViewById(R.id.pb_main_fetching_user_data);

        textViewDisplayName = findViewById(R.id.text_view_main_display_main);
        textViewORMobile = findViewById(R.id.text_view_main_or_mobile);
        textViewUserCategory = findViewById(R.id.text_view_main_user_category);

        userFound = false;
    }

    // All initialization related to firebase will go here =========================================
    private void initializeFirebase() {
        // Initialize Firebase Auth
        // Declare an instance of FirebaseAuth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();

        // Path Reference to the root of the Realtime Database
        referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    // Read firebase database's owner's path =======================================================
    private void readFirebaseOwnerDetails() {
        // Path reference to the User "Owner"
        DatabaseReference referenceUserOwner = referenceRoot.child(FIREBASE_USER_OWNER_PATH);

        referenceUserOwner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    progressBarFetchingData.setVisibility(View.GONE);

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        OwnersOfHouse ownersOfHouse = userSnapshot.getValue(OwnersOfHouse.class);

                        assert ownersOfHouse != null;

                        String landlordMobile = ownersOfHouse.getLandlordMobile();
                        String landladyMobile = ownersOfHouse.getLandladyMobile();
                        String alternateMobile = ownersOfHouse.getAlternateMobile();

                        displayName = ownersOfHouse.getDisplayName();

                        if (landlordMobile != null) {
                            if (landladyMobile != null) {
                                if (alternateMobile != null) {
                                    if (thisUserMobileNumber.equals(landlordMobile) ||
                                            thisUserMobileNumber.equals(landladyMobile) ||
                                            thisUserMobileNumber.equals(alternateMobile)) {
                                        userFound = true;
                                        userCategory = USER_OWNER;
                                    }
                                } else {
                                    if (thisUserMobileNumber.equals(landlordMobile) ||
                                            thisUserMobileNumber.equals(landladyMobile)) {
                                        userFound = true;
                                        userCategory = USER_OWNER;
                                    }
                                }
                            } else if (alternateMobile != null) {
                                if (thisUserMobileNumber.equals(landlordMobile) || thisUserMobileNumber.equals(alternateMobile)) {
                                    userFound = true;
                                    userCategory = USER_OWNER;
                                }
                            } else {
                                if (thisUserMobileNumber.equals(landlordMobile)) {
                                    userFound = true;
                                    userCategory = USER_OWNER;
                                }
                            }
                        } else if (landladyMobile != null) {
                            if (alternateMobile != null) {
                                if (thisUserMobileNumber.equals(landladyMobile) || thisUserMobileNumber.equals(alternateMobile)) {
                                    userFound = true;
                                    userCategory = USER_OWNER;
                                }
                            } else {
                                if (thisUserMobileNumber.equals(landladyMobile)) {
                                    userFound = true;
                                    userCategory = USER_OWNER;
                                    displayMobile = landladyMobile;
                                }
                            }
                        } else {
                            if (thisUserMobileNumber.equals(alternateMobile)) {
                                userFound = true;
                                userCategory = USER_OWNER;
                            }
                        }
                    }

                    if (userFound) {
                        setAccountDetails(userCategory);
                    } else {
                        readFirebaseRenterDetails();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Read firebase database's renter's path ======================================================
    private void readFirebaseRenterDetails() {
        // Path reference to the User "Renter"
        DatabaseReference referenceUserRenter = referenceRoot.child(FIREBASE_USER_RENTER_PATH);

        referenceUserRenter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot renterSnapshot : dataSnapshot.getChildren()) {
                        RentersOfHouse rentersOfHouse = renterSnapshot.getValue(RentersOfHouse.class);

                        assert rentersOfHouse != null;
                        String maleName = rentersOfHouse.getRenterNameMale();
                        String femaleName = rentersOfHouse.getRenterNameFemale();
                        String maleMobile = rentersOfHouse.getRenterMaleMobile();
                        String femaleMobile = rentersOfHouse.getRenterFemaleMobile();
                        String altMobile = rentersOfHouse.getAlternateMobile();
                        displayName = rentersOfHouse.getDisplayName();
                        userCategory = USER_RENTER;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Set the details of the owner/renter =========================================================
    private void setAccountDetails(String userCategory) {
        textViewDisplayName.setText(displayName);
        textViewORMobile.setText(displayMobile);
        textViewUserCategory.setText(String.format("House %s", userCategory));

        switch (userCategory) {
            case USER_OWNER:
                buttonOwnersRenters.setText("My Renters");
                break;

            case USER_RENTER:
                buttonOwnersRenters.setText("My Owners");
                break;
        }
    }

    private void showToast(String stringMessage) {
        Toast.makeText(this, stringMessage, Toast.LENGTH_SHORT).show();
    }

    private void setButtonClickListeners() {
        buttonOwnersRenters.setOnClickListener(this);
        buttonAboutApp.setOnClickListener(this);
    }

    @Override
    public void onClick(@NotNull View view) {
        switch (view.getId()) {
            case R.id.button_main_owners_renters:
                if (userFound) {
                    Intent intent = new Intent(PayKeeperMainActivity.this,
                            OwnersRentersListActivity.class);
                    intent.putExtra(KEY_USER, userCategory);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please wait while reading...", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.button_main_about_app:
                try {
                    PackageInfo pInfo = getApplicationContext().getPackageManager()
                            .getPackageInfo(getPackageName(), 0);
                    String versionName = String.format("Version%s", pInfo.versionName.substring(3));

                    showToast(versionName);
                } catch (PackageManager.NameNotFoundException exception) {
                    exception.printStackTrace();
                }
                break;
        }
    }
}
