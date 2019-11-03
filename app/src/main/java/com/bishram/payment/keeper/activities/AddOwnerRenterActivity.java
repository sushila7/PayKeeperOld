package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.OwnerRenterList;
import com.bishram.payment.keeper.models.OwnersOfHouse;
import com.bishram.payment.keeper.models.RentersOfHouse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.bishram.payment.keeper.Constants.FIREBASE_OWNERS_RENTED_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USER_OWNER_PATH;
import static com.bishram.payment.keeper.Constants.FIREBASE_USER_RENTER_PATH;
import static com.bishram.payment.keeper.Constants.KEY_USER;
import static com.bishram.payment.keeper.Constants.USER_OWNER;
import static com.bishram.payment.keeper.Constants.USER_RENTER;

public class AddOwnerRenterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSave;
    private Button buttonDiscard;
    private Button buttonContinue;

    private EditText editTextMobile;
    private EditText editTextName;
    private EditText editTextRent;

    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;

    private ArrayList<OwnersOfHouse> arrayListOwner;
    private ArrayList<RentersOfHouse> arrayListRenter;

    private boolean userAlreadyAvailable;
    private boolean readingFdbCompleted;

    private String stringUser;
    private String stringNickname;
    private String stringMobile;
    private String stringRentPaying;
    private String stringOwnerUID;
    private String stringRenterUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_owner_renter);

        getIntents();

        initializeViews();

        setButtonClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        readFirebaseOwnersDetails();
    }

    private void getIntents() {

    }

    private void initializeViews() {
        buttonSave = findViewById(R.id.button_add_or_save);
        buttonContinue = findViewById(R.id.button_add_or_check_mob);
        buttonDiscard = findViewById(R.id.button_add_or_discard);

        editTextMobile = findViewById(R.id.edit_text_add_or_mob_no);
        editTextName = findViewById(R.id.edit_text_add_or_name);
        editTextRent = findViewById(R.id.edit_text_add_or_rent);


        radioButtonMale = findViewById(R.id.radio_button_add_or_male);
        radioButtonFemale = findViewById(R.id.radio_button_add_or_female);

        stringUser = getIntent().getStringExtra(KEY_USER);

        arrayListOwner = new ArrayList<>();
        arrayListRenter = new ArrayList<>();
    }

    private void setButtonClickListener() {
        buttonSave.setOnClickListener(this);
        buttonDiscard.setOnClickListener(this);
        buttonContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_or_check_mob:
                if (readingFdbCompleted) {
                    switch (stringUser) {
                        case USER_OWNER:
                            String stringEtMobile = editTextMobile.getText().toString().trim();
                            String stringPrimaryMobile;
                            String stringSecondaryMobile;
                            String stringAlternateMobile;
                            stringRentPaying = editTextRent.getText().toString().trim();
                            userAlreadyAvailable = false;

                            for (int i = 0; i < arrayListRenter.size(); i++) {
                                stringPrimaryMobile = arrayListRenter.get(i).getRenterMaleMobile();
                                stringSecondaryMobile = arrayListRenter.get(i).getRenterFemaleMobile();
                                stringAlternateMobile = arrayListRenter.get(i).getAlternateMobile();

                                if (stringPrimaryMobile != null) {
                                    if (stringSecondaryMobile != null) {
                                        if (stringAlternateMobile != null) {
                                            if (stringEtMobile.equals(stringPrimaryMobile.substring(3)) ||
                                                    stringEtMobile.equals(stringSecondaryMobile.substring(3)) ||
                                                    stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                                userAlreadyAvailable = true;
                                                stringNickname = arrayListRenter.get(i).getDisplayName();
                                                stringRenterUID = arrayListRenter.get(i).getUniqueID();
                                            }
                                        } else {
                                            if (stringEtMobile.equals(stringPrimaryMobile.substring(3)) ||
                                                    stringEtMobile.equals(stringSecondaryMobile.substring(3))) {
                                                userAlreadyAvailable = true;
                                                stringNickname = arrayListRenter.get(i).getDisplayName();
                                                stringRenterUID = arrayListRenter.get(i).getUniqueID();
                                            }
                                        }
                                    } else if (stringAlternateMobile != null){
                                        if (stringEtMobile.equals(stringPrimaryMobile.substring(3)) ||
                                                stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                            stringNickname = arrayListRenter.get(i).getDisplayName();
                                            stringRenterUID = arrayListRenter.get(i).getUniqueID();
                                        }
                                    } else {
                                        if (stringEtMobile.equals(stringPrimaryMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                            stringNickname = arrayListRenter.get(i).getDisplayName();
                                            stringRenterUID = arrayListRenter.get(i).getUniqueID();
                                        }
                                    }
                                } else if (stringSecondaryMobile != null) {
                                    if (stringAlternateMobile != null) {
                                        if (stringEtMobile.equals(stringSecondaryMobile.substring(3)) ||
                                                stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                            stringNickname = arrayListRenter.get(i).getDisplayName();
                                            stringRenterUID = arrayListRenter.get(i).getUniqueID();
                                        }
                                    } else {
                                        if (stringEtMobile.equals(stringSecondaryMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                            stringNickname = arrayListRenter.get(i).getDisplayName();
                                            stringRenterUID = arrayListRenter.get(i).getUniqueID();
                                        }
                                    }
                                } else {
                                    if (stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                        userAlreadyAvailable = true;
                                        stringNickname = arrayListRenter.get(i).getDisplayName();
                                        stringRenterUID = arrayListRenter.get(i).getUniqueID();
                                    }
                                }
                            }

                            if (userAlreadyAvailable) {
                                editTextName.setText(stringNickname);
                                editTextRent.requestFocus();
                            } else {
                                editTextName.setText("");
                                editTextName.requestFocus();
                            }
                            break;

                        case USER_RENTER:
                            stringEtMobile = editTextMobile.getText().toString().trim();
                            for (int i = 0; i < arrayListOwner.size(); i++) {
                                stringPrimaryMobile = arrayListOwner.get(i).getLandlordMobile();
                                stringSecondaryMobile = arrayListOwner.get(i).getLandladyMobile();
                                stringAlternateMobile = arrayListOwner.get(i).getAlternateMobile();

                                if (stringPrimaryMobile != null) {
                                    if (stringSecondaryMobile != null) {
                                        if (stringAlternateMobile != null) {
                                            if (stringEtMobile.equals(stringPrimaryMobile.substring(3)) ||
                                                stringEtMobile.equals(stringSecondaryMobile.substring(3)) ||
                                                stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                                userAlreadyAvailable = true;
                                            }
                                        } else {
                                            if (stringEtMobile.equals(stringPrimaryMobile.substring(3)) ||
                                                    stringEtMobile.equals(stringSecondaryMobile.substring(3))) {
                                                userAlreadyAvailable = true;
                                            }
                                        }
                                    } else if (stringAlternateMobile != null){
                                        if (stringEtMobile.equals(stringPrimaryMobile.substring(3)) ||
                                                stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                        }
                                    } else {
                                        if (stringEtMobile.equals(stringPrimaryMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                        }
                                    }
                                } else if (stringSecondaryMobile != null) {
                                    if (stringAlternateMobile != null) {
                                        if (stringEtMobile.equals(stringSecondaryMobile.substring(3)) ||
                                                stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                        }
                                    } else {
                                        if (stringEtMobile.equals(stringSecondaryMobile.substring(3))) {
                                            userAlreadyAvailable = true;
                                        }
                                    }
                                } else {
                                    if (stringEtMobile.equals(stringAlternateMobile.substring(3))) {
                                        userAlreadyAvailable = true;
                                    }
                                }
                            }

                            if (userAlreadyAvailable) {

                            }
                            break;
                    }
                } else {
                    Toast.makeText(this, "Please wait while reading...", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_add_or_save:
                if (readingFdbCompleted) {
                    switch (stringUser) {
                        case USER_OWNER:
                            if (userAlreadyAvailable) { //renter is available
                                stringRentPaying = editTextRent.getText().toString().trim();

                                if (!stringRentPaying.isEmpty()) {
                                    DatabaseReference ownerRentedRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_OWNERS_RENTED_PATH);

                                    OwnerRenterList ownerRenterList = new OwnerRenterList(stringNickname, stringRentPaying, "Active", stringRenterUID);
                                    ownerRentedRef.child(stringOwnerUID).child(stringRenterUID).setValue(ownerRenterList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddOwnerRenterActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(AddOwnerRenterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Error!\nPlease try again later", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                stringNickname = editTextName.getText().toString().trim();
                                stringMobile = editTextMobile.getText().toString().trim();
                                stringRentPaying = editTextRent.getText().toString().trim();

                                DatabaseReference referenceToRenter = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USER_RENTER_PATH);
                                final String pushKey = referenceToRenter.push().getKey();

                                assert pushKey != null;

                                final RentersOfHouse rentersOfHouse = new RentersOfHouse(stringNickname, null, "Mr. & Mrs.", null, null, stringMobile, pushKey);
                                referenceToRenter.child(pushKey).setValue(rentersOfHouse).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference referenceToOwnerRented = FirebaseDatabase.getInstance().getReference().child(FIREBASE_OWNERS_RENTED_PATH);

                                            OwnerRenterList ownerRenterList = new OwnerRenterList(stringNickname, stringRentPaying, "Active", pushKey);
                                            referenceToOwnerRented.child(stringOwnerUID).child(pushKey).setValue(ownerRenterList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(AddOwnerRenterActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(AddOwnerRenterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                            break;

                        case USER_RENTER:
                            break;
                    }
                } else {
                    Toast.makeText(this, "Please wait while reading...", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_add_or_discard:
                Toast.makeText(this, "User discarded", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void readFirebaseOwnersDetails() {
        DatabaseReference referenceRoot = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference referenceOwner = referenceRoot.child(FIREBASE_USER_OWNER_PATH);

        readingFdbCompleted = false;

        referenceOwner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListOwner.clear();

                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshotOwner : dataSnapshot.getChildren()) {
                        OwnersOfHouse ownersOfHouse = snapshotOwner.getValue(OwnersOfHouse.class);

                        assert ownersOfHouse != null;
                        String maleName = ownersOfHouse.getLandlordName();
                        String femaleName = ownersOfHouse.getLandladyName();
                        String maleMobile = ownersOfHouse.getLandlordMobile();
                        String femaleMobile = ownersOfHouse.getLandladyMobile();
                        String altMobile = ownersOfHouse.getAlternateMobile();
                        stringOwnerUID = ownersOfHouse.getUniqueID();
                        arrayListOwner.add(ownersOfHouse);

                        String etMob = editTextMobile.getText().toString().trim();

                        if (maleMobile != null) {
                            if (femaleMobile != null) {
                                if (altMobile != null) {
                                    if (etMob.equals(maleMobile.substring(3))
                                        || etMob.equals(femaleMobile.substring(3))
                                        || etMob.equals(altMobile.substring(3))) {
                                        editTextName.setText(maleName);
                                    }
                                } else {
                                    if (etMob.equals(maleMobile.substring(3)) || etMob.equals(femaleMobile.substring(3))) {
                                        editTextName.setText(maleName);
                                    }
                                }
                            } else if (altMobile != null) {
                                if (etMob.equals(maleMobile.substring(3)) ||etMob.equals(altMobile.substring(3))) {
                                    editTextName.setText(maleName);
                                }
                            } else {
                                if (etMob.equals(maleMobile.substring(3))) {
                                    editTextName.setText(maleName);
                                }
                            }
                        } else if (femaleMobile != null) {
                            if (altMobile != null) {
                                if (etMob.equals(femaleMobile.substring(3)) || etMob.equals(altMobile.substring(3))) {
                                    editTextName.setText(femaleName);
                                }
                            } else {
                                if (etMob.equals(femaleMobile.substring(3))) {
                                    editTextName.setText(femaleName);
                                }
                            }
                        } else {
                            if (etMob.equals(altMobile.substring(3))) {
                                editTextName.setText(maleName);
                            }
                        }
                    }
                }

                readFirebaseRentersDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readFirebaseRentersDetails() {
        DatabaseReference referenceRoot = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference referenceRenter = referenceRoot.child(FIREBASE_USER_RENTER_PATH);

        referenceRenter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListRenter.clear();

                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot renterSnapshot : dataSnapshot.getChildren()) {
                        RentersOfHouse rentersOfHouse = renterSnapshot.getValue(RentersOfHouse.class);

                        assert rentersOfHouse != null;
                        String maleName = rentersOfHouse.getRenterNameMale();
                        String femaleName = rentersOfHouse.getRenterNameFemale();
                        String maleMobile = rentersOfHouse.getRenterMaleMobile();
                        String femaleMobile = rentersOfHouse.getRenterFemaleMobile();
                        String altMobile = rentersOfHouse.getAlternateMobile();
                        stringRenterUID = rentersOfHouse.getUniqueID();
                        arrayListRenter.add(rentersOfHouse);

                        if (maleMobile != null && femaleMobile != null && altMobile != null) {
                        }

                        if (maleMobile != null && femaleMobile != null && altMobile == null) {
                        }

                        if (maleMobile != null && femaleMobile == null && altMobile != null) {
                            String etMob = editTextMobile.getText().toString().trim();
                            if (etMob.equals(maleMobile.substring(3)) || etMob.equals(altMobile.substring(3))) {
                                assert maleName != null;
                                editTextName.setText(maleName);
                                userAlreadyAvailable = true;
                            }
                        }

                        if (maleMobile == null && femaleMobile != null && altMobile != null) {
                        }

                        if (maleMobile != null && femaleMobile == null && altMobile == null) {
                        }

                        if (maleMobile == null && femaleMobile != null && altMobile == null) {
                        }

                        if (maleMobile == null && femaleMobile == null && altMobile != null) {
                        }

                        if (maleMobile == null && femaleMobile == null && altMobile == null) {
                        }
                    }
                }

                readingFdbCompleted = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validateFields() {
        String mobileNumber = editTextMobile.getText().toString().trim();
        String userName = editTextName.getText().toString().trim();

        if (!mobileNumber.isEmpty() && !userName.isEmpty() &&
                (radioButtonMale.isChecked() || radioButtonFemale.isChecked())) {
            saveToFirebase(userName, mobileNumber);
        } else {
            Toast.makeText(this, "Error!\nPlease try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveToFirebase(final String userName, String mobileNumber) {
        if (stringUser.equals(USER_OWNER)) {
            DatabaseReference renterRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USER_RENTER_PATH);
            final String pushKey = renterRef.push().getKey();

            RentersOfHouse rentersOfHouse;

            if (radioButtonMale.isChecked()) {
                rentersOfHouse = new RentersOfHouse(userName, null, null, null, null, mobileNumber, pushKey);
            } else {
                rentersOfHouse = new RentersOfHouse(null, userName, null, null, null, mobileNumber, pushKey);
            }

            if (userAlreadyAvailable) {
                DatabaseReference ownerRentedRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_OWNERS_RENTED_PATH);

                OwnerRenterList ownerRenterList = new OwnerRenterList(userName, stringRentPaying, "Active", stringRenterUID);
                ownerRentedRef.child(stringOwnerUID).child(stringRenterUID).setValue(ownerRenterList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddOwnerRenterActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddOwnerRenterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                assert pushKey != null;
                renterRef.child(pushKey).setValue(rentersOfHouse).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddOwnerRenterActivity.this, "Saved " + userName, Toast.LENGTH_SHORT).show();

                            DatabaseReference ownerRentedRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_OWNERS_RENTED_PATH);

                            OwnerRenterList ownerRenterList = new OwnerRenterList(userName, stringRentPaying, "Active", pushKey);
                            ownerRentedRef.child(stringOwnerUID).child(pushKey).setValue(ownerRenterList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddOwnerRenterActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(AddOwnerRenterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(AddOwnerRenterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }
    }
}
