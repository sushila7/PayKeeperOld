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

import java.util.Objects;

import static com.bishram.payment.keeper.Constants.FIREBASE_USER_OWNER_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USER_RENTER_PATH;

public class PayKeeperMainActivity extends AppCompatActivity {

    // Declare view instances below
    private Button buttonOwnersRenters;
    private Button buttonAboutApp;

    private ProgressBar progressBarFetchingData;

    private TextView textViewDisplayName;
    private TextView textViewORMobile;
    private TextView textViewUserCategory;

    // Declare an instance of FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Declare an instance of root path of Realtime Database
    private DatabaseReference referenceRoot;

    private boolean orUserFound;

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

        if (!checkUser()) {
            // No logged user if found. In case Goto login activity
            startActivity(new Intent(PayKeeperMainActivity.this, PayKeeperLoginActivity.class));
            finish();
        } else {
            // Logged user is found
            thisUserMobileNumber = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
            readFirebaseOwnerDetails();
        }

        buttonAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageInfo pInfo = getApplicationContext().getPackageManager()
                            .getPackageInfo(getPackageName(), 0);
                    String versionName = String.format("Version%s", pInfo.versionName.substring(3));

                    showToast(versionName);
                } catch (PackageManager.NameNotFoundException exception) {
                    exception.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // All initialization related to UI views will go here =========================================
    private void initializeViews() {
        buttonOwnersRenters = findViewById(R.id.button_main_owners_renters);
        buttonAboutApp = findViewById(R.id.button_main_about_app);

        progressBarFetchingData = findViewById(R.id.pb_main_fetching_user_data);

        textViewDisplayName = findViewById(R.id.text_view_main_display_main);
        textViewORMobile = findViewById(R.id.text_view_main_or_mobile);
        textViewUserCategory = findViewById(R.id.text_view_main_user_category);
    }

    // All initialization related to firebase will go here =========================================
    private void initializeFirebase() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Path Reference to the root of the Realtime Database
        referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    // Checks the user whether he/she is authenticated or not ======================================
    private boolean checkUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Return "true" or "false" according to the logged in condition of user
        // True if any user is logged in
        // False if no user is logged in
        return currentUser != null;
    }

    // Read firebase database's owner's path =======================================================
    private void readFirebaseOwnerDetails() {
        // Path reference to the User "Owner"
        DatabaseReference referenceUserOwner = referenceRoot.child(FIREBASE_USER_OWNER_PATH);

        progressBarFetchingData.setVisibility(View.VISIBLE);

        referenceUserOwner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orUserFound = false;

                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    progressBarFetchingData.setVisibility(View.GONE);

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        OwnersOfHouse ownersOfHouse = userSnapshot.getValue(OwnersOfHouse.class);

                        assert ownersOfHouse != null;
                        String lordName = ownersOfHouse.getLandlordName();
                        String ladyName = ownersOfHouse.getLandladyName();
                        displayName = ownersOfHouse.getDisplayName();
                        String lordMobile = ownersOfHouse.getLandlordMobile();
                        String ladyMobile = ownersOfHouse.getLandladyMobile();
                        String altMobile = ownersOfHouse.getAlternateMobile();
                        userCategory = "House Owner";

                        if (lordMobile != null && ladyMobile != null && altMobile != null) {
                            showToast("All mobile available");
                            orUserFound = true;
                        }

                        if (lordMobile != null && ladyMobile != null && altMobile == null) {
                            showToast("Alternate mobile not available");
                            orUserFound = true;
                        }

                        if (lordMobile != null && ladyMobile == null && altMobile != null) {
                            showToast("Landlady's mobile not available");
                            orUserFound = true;
                        }

                        if (lordMobile == null && ladyMobile != null && altMobile != null) {
                            showToast("Landlord's mobile not available");
                            orUserFound = true;
                        }

                        if (lordMobile != null && ladyMobile == null && altMobile == null) {
                            showToast("Only landlord's mobile available");
                            orUserFound = true;
                        }

                        if (lordMobile == null && ladyMobile != null && altMobile == null) {
                            if (thisUserMobileNumber.equals(ladyMobile)) {
                                orUserFound = true;

                                displayMobile = ladyMobile;
                                toastMessage = ladyMobile;
                                if (lordName != null) {
                                    if (ladyName != null) {
                                        toastMessage = String.format("%s\n%s\n%s\n%s", toastMessage, lordName, ladyName, displayName);
                                    } else {
                                        toastMessage = String.format("%s\n%s\n%s", toastMessage, lordName, displayName);
                                    }
                                } else {
                                    toastMessage = String.format("%s\n%s\n%s", toastMessage, ladyName, displayName);
                                }
                            }
                        }

                        if (lordMobile == null && ladyMobile == null && altMobile != null) {
                            showToast("Only alternate mobile available");
                            orUserFound = true;
                        }

                        if (lordMobile == null && ladyMobile == null && altMobile == null) {
                            showToast("It is impossible case");
                            orUserFound = true;
                        }
                    }

                    if (!orUserFound) {
                        readFirebaseRenterDetails();
                    } else {
                        setAccountDetails("Owner");
                    }
                } else {
                    // No owner was found
                    // So, read renter's path
                    readFirebaseRenterDetails();
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
                        orUserFound = false;

                        assert rentersOfHouse != null;
                        String maleName = rentersOfHouse.getRenterNameMale();
                        String femaleName = rentersOfHouse.getRenterNameFemale();
                        String maleMobile = rentersOfHouse.getRenterMaleMobile();
                        String femaleMobile = rentersOfHouse.getRenterFemaleMobile();
                        String altMobile = rentersOfHouse.getAlternateMobile();
                        displayName = rentersOfHouse.getDisplayName();
                        userCategory = "House Renter";

                        if (maleMobile != null && femaleMobile != null && altMobile != null) {
                            showToast("All mobile available");
                            orUserFound = true;
                        }

                        if (maleMobile != null && femaleMobile != null && altMobile == null) {
                            showToast("Alternate mobile not available");
                            orUserFound = true;
                        }

                        if (maleMobile != null && femaleMobile == null && altMobile != null) {
                            orUserFound = true;

                            displayMobile = maleMobile;
                        }

                        if (maleMobile == null && femaleMobile != null && altMobile != null) {
                            showToast("Landlord's mobile not available");
                            orUserFound = true;
                        }

                        if (maleMobile != null && femaleMobile == null && altMobile == null) {
                            showToast("Only landlord's mobile available");
                            orUserFound = true;
                        }

                        if (maleMobile == null && femaleMobile != null && altMobile == null) {
                            if (thisUserMobileNumber.equals(femaleMobile)) {
                                orUserFound = true;

                                toastMessage = femaleMobile;
                                if (maleName != null) {
                                    if (femaleName != null) {
                                        toastMessage = String.format("%s\n%s\n%s\n%s", toastMessage, maleName, femaleName, displayName);
                                    } else {
                                        toastMessage = String.format("%s\n%s\n%s", toastMessage, maleName, displayName);
                                    }
                                } else {
                                    toastMessage = String.format("%s\n%s\n%s", toastMessage, femaleName, displayName);
                                }
                            }
                        }

                        if (maleMobile == null && femaleMobile == null && altMobile != null) {
                            showToast("Only alternate mobile available");
                            orUserFound = true;
                        }

                        if (maleMobile == null && femaleMobile == null && altMobile == null) {
                            showToast("It is impossible case");
                            orUserFound = true;
                        }
                    }

                    if (!orUserFound) {
                        startActivity(new Intent(PayKeeperMainActivity.this, UserRegistrationActivity.class));
                        finish();
                    } else {
                        setAccountDetails("Renter");
                    }
                } else {
                    startActivity(new Intent(PayKeeperMainActivity.this, UserRegistrationActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Set the details of the owner/renter =========================================================
    private void setAccountDetails(String userCategory2) {
        textViewDisplayName.setText(displayName);
        textViewORMobile.setText(displayMobile);
        textViewUserCategory.setText(userCategory);

        switch (userCategory2) {
            case "Owner":
                buttonOwnersRenters.setText("All Renters");
                buttonOwnersRenters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PayKeeperMainActivity.this, OwnersRentersListActivity.class));
                    }
                });
                break;

            case "Renter":
                buttonOwnersRenters.setText("All Owners");
                buttonOwnersRenters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PayKeeperMainActivity.this, OwnersRentersListActivity.class));
                    }
                });
                break;
        }
    }

    private void showToast(String stringMessage) {
        Toast.makeText(this, stringMessage, Toast.LENGTH_SHORT).show();
    }
}
