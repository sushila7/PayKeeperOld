package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.OwnerRenterList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.bishram.payment.keeper.Constants.COUNTRY_CODE_INDIA;
import static com.bishram.payment.keeper.Constants.FIREBASE_OWNERS_RENTED_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_RENTERS_OWNED_PATH;
import static com.bishram.payment.keeper.Constants.KEY_CATEGORY;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class AddOwnerRenterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSave;
    private Button buttonDiscard;

    private EditText editTextMobile;
    private EditText editTextName;
    private EditText editTextRent;

    private DatabaseReference referenceRoot;

    private String mCategory;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_owner_renter);

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

        editTextName = findViewById(R.id.edit_text_add_or_name);
        editTextMobile = findViewById(R.id.edit_text_add_or_mob_num);
        editTextRent = findViewById(R.id.edit_text_add_or_rent);
    }

    private void initializeFirebaseOb() {
         referenceRoot = FirebaseDatabase.getInstance().getReference();
    }

    private void setButtonClickListener() {
        buttonSave.setOnClickListener(this);
        buttonDiscard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

                                        String mPushKey = referenceToOwner.push().getKey();

                                        assert mPushKey != null;
                                        referenceToOwner.child(mPushKey).setValue(new OwnerRenterList(stringName, stringPhone, stringRent, "Active", mPushKey)).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                        break;

                                    case USER_RENTER:
                                        DatabaseReference referenceToRenter = referenceRoot.child(FIREBASE_RENTERS_OWNED_PATH).child(mUid);

                                        mPushKey = referenceToRenter.push().getKey();

                                        assert mPushKey != null;
                                        referenceToRenter.child(mPushKey).setValue(new OwnerRenterList(stringName, stringPhone, stringRent, "Active", mPushKey)).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                break;

            case R.id.button_add_or_discard:
                Toast.makeText(this, "You discarded", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
