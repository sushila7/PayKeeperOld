package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.OwnersOfHouse;
import com.bishram.payment.keeper.models.RentersOfHouse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.Objects;

import static com.bishram.payment.keeper.Constants.FIREBASE_USER_OWNER_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USER_RENTER_PATH;

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private Button buttonDiscard;

    private CheckBox checkBoxSelf;

    private EditText editTextORNameSelf;
    private EditText editTextORNameSpouse;
    private EditText editTextDisplayName;
    private EditText editTextORMobSelf;
    private EditText editTextORMobSpouse;
    private EditText editTextAltMobile;

    private RadioButton radioButtonOwner;
    private RadioButton radioButtonRenter;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    private RadioGroup radioGroupGender;

    private int count = 0;

    private String authMobileNumber;
    private String selfName;
    private String spouseName;
    private String displayName;
    private String selfMobile;
    private String spouseMobile;
    private String altMobile;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        // Initialize xml view objects
        initializeViews();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            authMobileNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        }

        registerClickListeners();

        editTextNameSelfTextWatcher();
    }

    private void initializeViews() {
        buttonRegister = findViewById(R.id.button_reg_register_user);
        buttonDiscard = findViewById(R.id.button_reg_discard_user);

        checkBoxSelf = findViewById(R.id.check_box_reg_self);

        editTextORNameSelf = findViewById(R.id.edit_text_reg_or_name_male);
        editTextORNameSpouse = findViewById(R.id.edit_text_reg_or_name_female);
        editTextDisplayName = findViewById(R.id.edit_text_reg_display_male);
        editTextORMobSelf = findViewById(R.id.edit_text_reg_or_mob_male);
        editTextORMobSpouse = findViewById(R.id.edit_text_reg_or_mob_female);
        editTextAltMobile = findViewById(R.id.edit_text_reg_alt_mob);

        radioButtonOwner = findViewById(R.id.radio_button_reg_owner);
        radioButtonRenter = findViewById(R.id.radio_button_reg_renter);
        radioButtonMale = findViewById(R.id.rb_reg_or_gender_male);
        radioButtonFemale = findViewById(R.id.rb_reg_or_gender_female);
        radioGroupGender = findViewById(R.id.rg_reg_or_gender);
    }

    private void registerClickListeners() {
        buttonRegister.setOnClickListener(this);
        buttonDiscard.setOnClickListener(this);

        checkBoxSelf.setOnClickListener(this);

        radioButtonOwner.setOnClickListener(this);
        radioButtonRenter.setOnClickListener(this);
        radioButtonMale.setOnClickListener(this);
        radioButtonFemale.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_reg_register_user:

                if (checkBoxSelf.isChecked()) {
                    if (radioButtonMale.isChecked()) {
                        selfName = editTextORNameSelf.getText().toString().trim();
                        spouseName = editTextORNameSpouse.getText().toString().trim();
                        displayName = editTextDisplayName.getText().toString().trim();
                        selfMobile = editTextORMobSelf.getText().toString().trim();
                        spouseMobile = editTextORMobSpouse.getText().toString().trim();
                        altMobile = editTextAltMobile.getText().toString().trim();

                        if (spouseName.isEmpty()) {
                            spouseName = null;
                        }

                        if (spouseMobile.isEmpty()) {
                            spouseMobile = null;
                        }

                        if (altMobile.isEmpty()) {
                            altMobile = null;
                        }

                        saveUserToFirebase();
                    } else if (radioButtonFemale.isChecked()) {
                        selfName = editTextORNameSelf.getText().toString().trim();
                        spouseName = editTextORNameSpouse.getText().toString().trim();
                        displayName = editTextDisplayName.getText().toString().trim();
                        selfMobile = editTextORMobSelf.getText().toString().trim();
                        spouseMobile = editTextORMobSpouse.getText().toString().trim();
                        altMobile = editTextAltMobile.getText().toString().trim();

                        if (spouseName.isEmpty()) {
                            spouseName = selfName;
                            selfName = null;
                        } else {
                            String tmp = spouseName;
                            spouseName = selfName;
                            selfName = tmp;
                        }

                        if (spouseMobile.isEmpty()) {
                            spouseMobile = selfMobile;
                            selfMobile = null;
                        } else {
                            String tmp = spouseMobile;
                            spouseMobile = selfMobile;
                            selfMobile = tmp;
                        }

                        if (altMobile.isEmpty()) {
                            altMobile = null;
                        }

                        saveUserToFirebase();
                    }
                } else {
                    selfName = editTextORNameSelf.getText().toString().trim();
                    spouseName = editTextORNameSpouse.getText().toString().trim();
                    displayName = editTextDisplayName.getText().toString().trim();
                    selfMobile = editTextORMobSelf.getText().toString().trim();
                    spouseMobile = editTextORMobSpouse.getText().toString().trim();
                    altMobile = editTextAltMobile.getText().toString().trim();

                    if (spouseName.isEmpty()) {
                        spouseName = null;
                    }

                    if (selfMobile.isEmpty()) {
                        selfMobile = null;
                    }

                    if (spouseMobile.isEmpty()) {
                        spouseMobile = null;
                    }

                    saveUserToFirebase();
                }
                break;

            case R.id.button_reg_discard_user:
                Toast.makeText(this, "User discarded to save details!", Toast.LENGTH_SHORT).show();
                finish();
                break;

            case R.id.check_box_reg_self:
                if (checkBoxSelf.isChecked()) {
                    // Disable all the edit text fields
                    enableDisableFields(false, "all");

                    radioButtonMale.setClickable(true);
                    radioButtonFemale.setClickable(true);

                    setSelfDetails();
                } else {
                    // Enable all the edit text fields
                    enableDisableFields(true, "all");

                    switch (userType) {
                        case "Owner":
                            radioGroupGender.clearCheck();
                            radioButtonMale.setEnabled(false);
                            radioButtonFemale.setEnabled(false);

                            setOwnerDetails();
                            break;

                        case "Renter":
                            radioGroupGender.clearCheck();
                            radioButtonMale.setEnabled(false);
                            radioButtonFemale.setEnabled(false);

                            setRenterDetails();
                            break;
                    }
                }
                break;

            case R.id.radio_button_reg_owner:
                userType = "Owner";

                radioButtonOwner.setClickable(false);
                radioButtonRenter.setClickable(true);

                checkBoxSelf.setEnabled(true);

                if (checkBoxSelf.isChecked()) {
                    setSelfDetails();
                } else {
                    switch (userType) {
                        case "Owner":
                            radioGroupGender.clearCheck();
                            radioButtonMale.setEnabled(false);
                            radioButtonFemale.setEnabled(false);

                            setOwnerDetails();
                            break;

                        case "Renter":
                            radioGroupGender.clearCheck();
                            radioButtonMale.setEnabled(false);
                            radioButtonFemale.setEnabled(false);

                            setRenterDetails();
                            break;
                    }
                }
                break;

            case R.id.radio_button_reg_renter:
                userType = "Renter";

                radioButtonOwner.setClickable(true);
                radioButtonRenter.setClickable(false);

                checkBoxSelf.setEnabled(true);

                if (checkBoxSelf.isChecked()) {
                    setSelfDetails();
                } else {
                    switch (userType) {
                        case "Owner":
                            radioGroupGender.clearCheck();
                            radioButtonMale.setEnabled(false);
                            radioButtonFemale.setEnabled(false);

                            setOwnerDetails();
                            break;

                        case "Renter":
                            radioGroupGender.clearCheck();
                            radioButtonMale.setEnabled(false);
                            radioButtonFemale.setEnabled(false);

                            setRenterDetails();
                            break;
                    }
                }
                break;

            case R.id.rb_reg_or_gender_male:
                radioButtonMale.setClickable(false);
                radioButtonFemale.setClickable(true);

                enableDisableFields(true, "gender");
                break;

            case R.id.rb_reg_or_gender_female:
                radioButtonMale.setClickable(true);
                radioButtonFemale.setClickable(false);

                enableDisableFields(true, "gender");
                break;
        }
    }

    private void enableDisableFields(boolean trueFalse, @NotNull String string) {
        editTextORNameSelf.setEnabled(trueFalse);
        editTextORNameSpouse.setEnabled(trueFalse);
        editTextDisplayName.setEnabled(trueFalse);
        editTextORMobSpouse.setEnabled(trueFalse);
        editTextAltMobile.setEnabled(trueFalse);

        if (string.equals("gender")) {
            editTextORNameSelf.requestFocus();
            editTextORMobSelf.setEnabled(false);
        } else {
            editTextORMobSelf.setEnabled(trueFalse);
        }
    }

    private void setSelfDetails() {
        radioButtonMale.setEnabled(true);
        radioButtonFemale.setEnabled(true);

        editTextORMobSelf.setText("");
        editTextORNameSelf.setHint("Your full name");
        editTextORNameSelf.requestFocus();
        editTextORMobSelf.setText(authMobileNumber.substring(3));
        editTextORMobSelf.setEnabled(false);

        editTextORNameSpouse.setText("");
        editTextORNameSpouse.setHint("Spouse full name");
        editTextORMobSpouse.setText("");
        editTextORMobSpouse.setHint("Spouse's mobile number");

        editTextDisplayName.setText("");
        editTextDisplayName.setHint("Account display name");

        editTextAltMobile.setText("");
        editTextAltMobile.setHint("Alternative mobile number");
    }

    private void setOwnerDetails() {
        editTextORNameSelf.setText("");
        editTextORNameSelf.setHint("Landlord's full name");
        editTextORNameSelf.requestFocus();
        editTextORMobSelf.setText("");
        editTextORMobSelf.setHint("Landlord's mobile number");

        editTextORNameSpouse.setText("");
        editTextORNameSpouse.setHint("Landlady's full name");
        editTextORMobSpouse.setText("");
        editTextORMobSpouse.setHint("Landlady's mobile number");

        editTextDisplayName.setText("");
        editTextDisplayName.setHint("Account display name");

        editTextAltMobile.setText(authMobileNumber.substring(3));
        editTextAltMobile.setEnabled(false);
    }

    private void setRenterDetails() {
        editTextORNameSelf.setText("");
        editTextORNameSelf.setHint("Male renter's full name");
        editTextORNameSelf.requestFocus();
        editTextORMobSelf.setText("");
        editTextORMobSelf.setHint("Male renter's mobile number");

        editTextORNameSpouse.setText("");
        editTextORNameSpouse.setHint("Female renter's full name");
        editTextORMobSpouse.setText("");
        editTextORMobSpouse.setHint("Female renter's mobile number");

        editTextDisplayName.setText("");
        editTextDisplayName.setHint("Account display name");

        editTextAltMobile.setText(authMobileNumber.substring(3));
        editTextAltMobile.setEnabled(false);
    }

    private void editTextNameSelfTextWatcher() {
        editTextORNameSelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String stringName = editTextORNameSelf.getText().toString().trim();

                if (stringName.length() > 2) {
                    buttonRegister.setEnabled(true);
                } else {
                    buttonRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextORNameSelf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String stringNameSelf = editTextORNameSelf.getText().toString().trim();
                    int indexForSurname = 0;

                    if (stringNameSelf.length() != 0) {
                        int length = stringNameSelf.length();

                        for (int i = length-1; i > 0; i--) {
                            char ch = stringNameSelf.charAt(i);

                            if (ch == ' ') {
                                count++;
                                indexForSurname = i+1;
                                break;
                            }
                        }
                    }

                    if (count == 0) {
                        editTextDisplayName.setText(String.format("Mr. & Mrs. %s", stringNameSelf));
                    } else if (count == 1) {
                        editTextDisplayName.setText(String.format("Mr. & Mrs. %s", stringNameSelf.substring(indexForSurname)));
                    }
                } else {
                    count = 0;
                }
            }
        });
    }

    private void saveUserToFirebase() {
        if (userType.equals("Owner")) {
            DatabaseReference referenceOwner = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USER_OWNER_PATH);
            String pushID = referenceOwner.push().getKey();

            if (pushID != null) {
                OwnersOfHouse ownersOfHouse = new OwnersOfHouse(selfName, spouseName, displayName, selfMobile, spouseMobile, altMobile, pushID);
                referenceOwner.child(pushID).setValue(ownersOfHouse).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserRegistrationActivity.this, "Saved successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UserRegistrationActivity.this,
                                    Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } else if (userType.equals("Renter")) {
            DatabaseReference referenceOwner = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USER_RENTER_PATH);
            String pushID = referenceOwner.push().getKey();

            if (pushID != null) {
                RentersOfHouse rentersOfHouse = new RentersOfHouse(selfName, spouseName, displayName, selfMobile, spouseMobile, altMobile, pushID);
                referenceOwner.child(pushID).setValue(rentersOfHouse).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserRegistrationActivity.this, "Saved successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserRegistrationActivity.this, PayKeeperMainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(UserRegistrationActivity.this,
                                    Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
}
