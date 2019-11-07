package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.bishram.payment.keeper.Constants.FIREBASE_USERS_OWNERS_BASIC_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USERS_RENTERS_BASIC_PATH;
import static com.bishram.payment.keeper.Constants.KEY_CATEGORY;
import static com.bishram.payment.keeper.Constants.KEY_PHONE_NUM;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class PayKeeperMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonOwnersRenters;
    private Button buttonMyPayments;
    private Button buttonAboutApp;
    private Button buttonSignOut;

    private ProgressBar progressBar;

    private TextView textViewName;
    private TextView textViewPhoneNum;
    private TextView textViewCategory;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference referenceUserOwnerBasic;
    private DatabaseReference referenceUserRenterBasic;

    private boolean readComplete;

    private String mPhoneNumber;
    private String mCategory;
    private String mUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The user interface (UI)
        setContentView(R.layout.activity_main_pay_keeper);

        boolean hasUser = checkUserHasLogged();

        readComplete = false;

        initializeFirebaseOb();
        initializeViewOb();
        setClickListeners();

        if (!hasUser) {
            mPhoneNumber = mUser.getPhoneNumber();
            mUid = mUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            readFirebaseDatabase();
        } else {
            Toast.makeText(this, "No user signed in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PayKeeperMainActivity.this, PayKeeperLoginActivity.class));
            finish();
        }
    }

    private void initializeFirebaseOb() {
        DatabaseReference referenceRootPath = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();

        referenceUserOwnerBasic = referenceRootPath.child(FIREBASE_USERS_OWNERS_BASIC_PATH);
        referenceUserRenterBasic = referenceRootPath.child(FIREBASE_USERS_RENTERS_BASIC_PATH);
    }

    private void initializeViewOb() {
        buttonOwnersRenters = findViewById(R.id.button_main_owners_renters);
        buttonMyPayments = findViewById(R.id.button_main_my_payments);
        buttonAboutApp = findViewById(R.id.button_main_about_app);
        buttonSignOut = findViewById(R.id.button_main_sign_out);

        progressBar = findViewById(R.id.pb_main_fetching_user_data);

        textViewName = findViewById(R.id.text_view_main_display_main);
        textViewPhoneNum = findViewById(R.id.text_view_main_or_mobile);
        textViewCategory = findViewById(R.id.text_view_main_user_category);
    }

    private void setClickListeners() {
        buttonMyPayments.setOnClickListener(this);
        buttonOwnersRenters.setOnClickListener(this);
        buttonAboutApp.setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);
    }

    private boolean checkUserHasLogged() {
        return mUser != null;
    }

    private void readFirebaseDatabase() {
        referenceUserOwnerBasic.child(mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {

                    for (DataSnapshot snapshotItem : dataSnapshot.getChildren()) {
                        if (Objects.equals(snapshotItem.getKey(), "userName")) {
                            String userName = snapshotItem.getValue(String.class);

                            mCategory = USER_OWNER;

                            textViewName.setText(userName);
                            textViewPhoneNum.setText(mPhoneNumber);
                            textViewCategory.setText(getString(R.string.tv_text_house_owner));

                            buttonMyPayments.setText(getString(R.string.btn_text_renter_payments));
                            buttonOwnersRenters.setText(getString(R.string.btn_text_my_renters));
                            buttonAboutApp.setText(getString(R.string.tv_text_about_app));
                            buttonSignOut.setText(getString(R.string.btn_text_sign_out));
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                    readComplete = true;
                } else {
                    referenceUserRenterBasic.child(mUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                for (DataSnapshot snapshotItem : dataSnapshot.getChildren()) {
                                    if (Objects.equals(snapshotItem.getKey(), "userName")) {
                                        String userName = snapshotItem.getValue(String.class);

                                        mCategory = USER_RENTER;

                                        textViewName.setText(userName);
                                        textViewPhoneNum.setText(mPhoneNumber);
                                        textViewCategory.setText(getString(R.string.tv_text_house_renter));

                                        buttonMyPayments.setText(getString(R.string.btn_text_my_payments));
                                        buttonOwnersRenters.setText(getString(R.string.btn_text_my_owners));
                                        buttonAboutApp.setText(getString(R.string.tv_text_about_app));
                                        buttonSignOut.setText(getString(R.string.btn_text_sign_out));
                                    }
                                }

                                progressBar.setVisibility(View.GONE);
                                readComplete = true;
                            } else {
                                Toast.makeText(PayKeeperMainActivity.this, getString(R.string.tst_text_register_plz), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(PayKeeperMainActivity.this, UserRegistrationActivity.class);
                                intent.putExtra(KEY_UID, mUid);
                                intent.putExtra(KEY_PHONE_NUM, mPhoneNumber);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(PayKeeperMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            readComplete = true;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PayKeeperMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                readComplete = true;
            }
        });
    }

    // Button click handling function will go here ================================================>
    @Override
    public void onClick(View view) {
        if (readComplete) {
            switch (view.getId()) {
                case R.id.button_main_owners_renters:
                    Intent intent = new Intent(PayKeeperMainActivity.this, OwnersRentersListActivity.class);
                    intent.putExtra(KEY_CATEGORY, mCategory);
                    intent.putExtra(KEY_UID, mUid);
                    startActivity(intent);
                    break;

                case R.id.button_main_my_payments:
                    Intent intent1 = new Intent(PayKeeperMainActivity.this, TransactionListActivity.class);
                    intent1.putExtra(KEY_CATEGORY, mCategory);
                    intent1.putExtra(KEY_UID, mUid);
                    startActivity(intent1);
                    break;

                case R.id.button_main_about_app:
                    try {
                        PackageInfo pInfo = getApplicationContext().getPackageManager()
                                .getPackageInfo(getPackageName(), 0);
                        String versionName = String.format("Version%s", pInfo.versionName.substring(3));

                        Toast.makeText(this, versionName, Toast.LENGTH_SHORT).show();
                    } catch (PackageManager.NameNotFoundException exception) {
                        exception.printStackTrace();
                    }
                    break;

                case R.id.button_main_sign_out:
                    mAuth.signOut();
                    startActivity(new Intent(PayKeeperMainActivity.this, PayKeeperLoginActivity.class));
                    finish();
                    break;
            }
        } else {
            Toast.makeText(this, "Please wait while reading...", Toast.LENGTH_LONG).show();
        }
    }
}