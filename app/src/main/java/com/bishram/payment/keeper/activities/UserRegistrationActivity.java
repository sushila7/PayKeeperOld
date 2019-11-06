package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bishram.payment.keeper.R;

import com.bishram.payment.keeper.models.UserBasicDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.bishram.payment.keeper.Constants.FIREBASE_USERS_OWNERS_BASIC_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USERS_RENTERS_BASIC_PATH;
import static com.bishram.payment.keeper.Constants.KEY_PHONE_NUM;
import static com.bishram.payment.keeper.Constants.KEY_UID;

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private Button buttonDiscard;

    private EditText editTextName;

    private LinearLayout layoutInProgress;

    private RadioButton radioButtonOwner;
    private RadioButton radioButtonRenter;

    private String mUid;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration_new);

        getIntents();

        initializeViewOb();

        setClickListeners();
    }

    private void getIntents() {
        mUid = getIntent().getStringExtra(KEY_UID);
        mPhoneNumber = getIntent().getStringExtra(KEY_PHONE_NUM);
    }

    private void initializeViewOb() {
        buttonDiscard = findViewById(R.id.button_reg_2_discard);
        buttonRegister = findViewById(R.id.button_reg_2_register);

        editTextName = findViewById(R.id.edit_text_reg_2_enter_name);

        layoutInProgress = findViewById(R.id.layout_reg_2_in_progress);

        radioButtonOwner = findViewById(R.id.radio_button_reg_2_owner);
        radioButtonRenter = findViewById(R.id.radio_button_reg_2_renter);
    }

    private void setClickListeners() {
        buttonDiscard.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.button_reg_2_discard:
                Toast.makeText(this, "You discarded", Toast.LENGTH_SHORT).show();
                finish();
                break;

            case R.id.button_reg_2_register:
                String stringName = editTextName.getText().toString().trim();

                if (!stringName.isEmpty()) {
                    if (radioButtonOwner.isChecked() || radioButtonRenter.isChecked()) {
                        buttonDiscard.setEnabled(false);
                        buttonRegister.setEnabled(false);
                        layoutInProgress.setVisibility(View.VISIBLE);

                        if (radioButtonOwner.isChecked()) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USERS_OWNERS_BASIC_PATH);
                            databaseReference.child(mUid).setValue(new UserBasicDetails(stringName, mPhoneNumber, mUid)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserRegistrationActivity.this, "You are registered.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(UserRegistrationActivity.this, PayKeeperMainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(UserRegistrationActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        } else if (radioButtonRenter.isChecked()) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USERS_RENTERS_BASIC_PATH);
                            databaseReference.child(mUid).setValue(new UserBasicDetails(stringName, mPhoneNumber, mUid)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserRegistrationActivity.this, "You are registered.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(UserRegistrationActivity.this, PayKeeperMainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(UserRegistrationActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Please select a category!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                    editTextName.requestFocus();
                }
                break;
        }
    }
}
