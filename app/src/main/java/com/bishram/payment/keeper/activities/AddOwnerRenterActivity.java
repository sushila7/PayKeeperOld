package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.OwnerRenterList;
import com.bishram.payment.keeper.models.UserBasicDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.bishram.payment.keeper.Constants.COUNTRY_CODE_INDIA;
import static com.bishram.payment.keeper.Constants.FIREBASE_OWNERS_RENTED_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_RENTERS_OWNED_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USERS_OWNERS_BASIC_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USERS_RENTERS_BASIC_PATH;
import static com.bishram.payment.keeper.Constants.KEY_CATEGORY;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class AddOwnerRenterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSave;
    private Button buttonDiscard;
    private Button buttonCheckUser;

    private EditText editTextMobile;
    private EditText editTextName;
    private EditText editTextRent;

    private LinearLayout layoutCheckUser;
    private LinearLayout layoutRegisterUser;

    private TextView textViewUserFound;

    private DatabaseReference referenceRoot;

    private boolean userFound;

    private String mCategory;
    private String mUid;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_owner_renter_new);

        getIntents();

        initializeViews();

        initializeFirebaseOb();

        setButtonClickListener();
    }

    private void getIntents() {
        mCategory = getIntent().getStringExtra(KEY_CATEGORY);
        mUid = getIntent().getStringExtra(KEY_UID);
    }

    private void initializeViews() {
        buttonSave = findViewById(R.id.button_add_or_save);
        buttonDiscard = findViewById(R.id.button_add_or_discard);
        buttonCheckUser = findViewById(R.id.button_add_or_check_user);

        editTextName = findViewById(R.id.edit_text_add_or_name);
        editTextMobile = findViewById(R.id.edit_text_add_or_mob_num);
        editTextRent = findViewById(R.id.edit_text_add_or_rent);

        layoutCheckUser = findViewById(R.id.layout_add_or_check_user);
        layoutRegisterUser = findViewById(R.id.layout_add_or_register_user);

        textViewUserFound = findViewById(R.id.text_view_add_or_user_found);
    }

    private void initializeFirebaseOb() {
         referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    private void setButtonClickListener() {
        buttonSave.setOnClickListener(this);
        buttonDiscard.setOnClickListener(this);
        buttonCheckUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_or_check_user:
                switch (mCategory) {
                    case USER_OWNER:
                        userFound = false;
                        readOwnerRenterBasic(referenceRoot.child(FIREBASE_USERS_RENTERS_BASIC_PATH));
                        break;

                    case USER_RENTER:
                        userFound = false;
                        readOwnerBasic();
                        break;
                }
                break;

            case R.id.button_add_or_save:
                String stringName = editTextName.getText().toString().trim();
                String stringPhone = String.format("%s%s", COUNTRY_CODE_INDIA, editTextMobile.getText().toString().trim());
                String stringRent = editTextRent.getText().toString().trim();

                if (!stringName.isEmpty()) {
                    if (!stringPhone.isEmpty()) {
                        if (stringPhone.length() == 13) {
                            if (!stringRent.isEmpty()) {
                                switch (mCategory) {
                                    case USER_OWNER:
                                        DatabaseReference referenceToOwner = referenceRoot.child(FIREBASE_OWNERS_RENTED_PATH).child(mUid);

                                        if (userUid != null) {
                                            referenceToOwner.child(userUid).setValue(new OwnerRenterList(stringName, stringPhone, stringRent, "Active", userUid)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(AddOwnerRenterActivity.this, "Renter added.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(AddOwnerRenterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(this, "Firebase location error!", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                    case USER_RENTER:
                                        DatabaseReference referenceToRenter = referenceRoot.child(FIREBASE_RENTERS_OWNED_PATH).child(mUid);

                                        referenceToRenter.child(userUid).setValue(new OwnerRenterList(stringName, stringPhone, stringRent, "Active", userUid)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AddOwnerRenterActivity.this, "Owner added.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(AddOwnerRenterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                }
                            } else {
                                if (mCategory.equals(USER_OWNER)) {
                                    Toast.makeText(this, "Please enter renter's monthly rent!", Toast.LENGTH_SHORT).show();
                                    editTextRent.requestFocus();
                                } else if (mCategory.equals(USER_RENTER)) {
                                    Toast.makeText(this, "Please enter your monthly rent!", Toast.LENGTH_SHORT).show();
                                    editTextRent.requestFocus();
                                }
                            }
                        } else {
                            Toast.makeText(this, "Invalid mobile number!", Toast.LENGTH_SHORT).show();
                            editTextMobile.requestFocus();
                        }
                    } else {
                        if (mCategory.equals(USER_OWNER)) {
                            Toast.makeText(this, "Please enter renter's mobile number!", Toast.LENGTH_SHORT).show();
                            editTextMobile.requestFocus();
                        } else if (mCategory.equals(USER_RENTER)) {
                            Toast.makeText(this, "Please enter owner's mobile number!", Toast.LENGTH_SHORT).show();
                            editTextMobile.requestFocus();
                        }
                    }
                } else {
                    if (mCategory.equals(USER_OWNER)) {
                        Toast.makeText(this, "Please enter renter's name!", Toast.LENGTH_SHORT).show();
                        editTextName.requestFocus();
                    } else if (mCategory.equals(USER_RENTER)) {
                        Toast.makeText(this, "Please enter owner's name!", Toast.LENGTH_SHORT).show();
                        editTextName.requestFocus();
                    }
                }

                Toast.makeText(this, userUid, Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_add_or_discard:
                Toast.makeText(this, "You discarded", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void readOwnerBasic() {
        final String stringMobile = String.format("%s%s", COUNTRY_CODE_INDIA, editTextMobile.getText().toString().trim());

        referenceRoot.child(FIREBASE_USERS_OWNERS_BASIC_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot ownerSnapshot : dataSnapshot.getChildren()) {
                        UserBasicDetails userBasicDetails = ownerSnapshot.getValue(UserBasicDetails.class);

                        assert userBasicDetails != null;
                        if (userBasicDetails.getUserMobile().equals(stringMobile)) {
                            layoutRegisterUser.setVisibility(View.VISIBLE);
                            textViewUserFound.setText("Owner found!");
                            editTextName.setText(userBasicDetails.getUserName());
                            editTextRent.requestFocus();
                            userUid = userBasicDetails.getUidAuth();
                            userFound = true;
                        }
                    }
                } else {
                    layoutRegisterUser.setVisibility(View.VISIBLE);
                    textViewUserFound.setText("No owner found!");
                }

                if (!userFound) {
                    layoutRegisterUser.setVisibility(View.VISIBLE);
                    textViewUserFound.setText("No owner found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddOwnerRenterActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readOwnerRenterBasic(DatabaseReference orDbReference) {
        final String stringMobile = String.format("%s%s", COUNTRY_CODE_INDIA, editTextMobile.getText().toString().trim());

        orDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot ownerSnapshot : dataSnapshot.getChildren()) {
                        UserBasicDetails userBasicDetails = ownerSnapshot.getValue(UserBasicDetails.class);

                        assert userBasicDetails != null;
                        if (userBasicDetails.getUserMobile().equals(stringMobile)) {
                            layoutRegisterUser.setVisibility(View.VISIBLE);
                            textViewUserFound.setText(String.format("%s found!", mCategory));
                            editTextName.setText(userBasicDetails.getUserName());
                            editTextRent.requestFocus();
                            userUid = userBasicDetails.getUidAuth();
                            userFound = true;
                        }
                    }
                } else {
                    layoutRegisterUser.setVisibility(View.VISIBLE);
                    textViewUserFound.setText("No user found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddOwnerRenterActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
