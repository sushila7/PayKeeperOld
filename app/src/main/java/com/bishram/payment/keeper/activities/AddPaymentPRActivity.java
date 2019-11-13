package com.bishram.payment.keeper.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bishram.payment.keeper.R;
import com.bishram.payment.keeper.models.RentPaidReceived;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.bishram.payment.keeper.Constants.FIREBASE_OWNER_RENTER_ALL_PAYMENT;
import static com.bishram.payment.keeper.Constants.FIREBASE_RENT_PAID_RECEIVED;
import static com.bishram.payment.keeper.Constants.KEY_CATEGORY;
import static com.bishram.payment.keeper.Constants.KEY_OWNER_RENTER_NAME;
import static com.bishram.payment.keeper.Constants.KEY_OWNER_RENTER_UID;
import static com.bishram.payment.keeper.Constants.KEY_UID;
import static com.bishram.payment.keeper.Constants.USER_OWNER;

public class AddPaymentPRActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSavePayment;
    private Button buttonDiscardPayment;

    private EditText editTextYearOfPayment;
    private EditText editTextMonthOfYear;
    private EditText editTextDateOfMonth;
    private EditText editTextDayOfWeek;
    private EditText editTextAmountPaidReceived;
    private EditText editTextPersonPaid;
    private EditText editTextPersonReceived;

    private LinearLayout layoutPreparing;
    private LinearLayout layoutSaveDiscard;

    private ScrollView scrollViewInputs;

    private boolean valid;

    private long noOfMyEntry;
    private long noOfHisEntry;

    private String mCategory;
    private String mORName;
    private String mORUid;
    private String mUid;
    private String mYearOfPayment;
    private String mMonthOfYear;
    private String mDateOfMonth;
    private String mDayOfWeek;
    private String mAmountPaidReceived;
    private String mPersonPaid;
    private String mPersonReceived;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_paid_received);

        getIntents();

        initializeViewOb();

        readSelfPayments();

        setClickListeners();

        getEditTexts();
    }

    private void getIntents() {
        mCategory = getIntent().getStringExtra(KEY_CATEGORY);
        mORName = getIntent().getStringExtra(KEY_OWNER_RENTER_NAME);
        mORUid = getIntent().getStringExtra(KEY_OWNER_RENTER_UID);
        mUid = getIntent().getStringExtra(KEY_UID);
    }

    private void initializeViewOb() {
        buttonSavePayment = findViewById(R.id.button_ppr_save_payment);
        buttonDiscardPayment = findViewById(R.id.button_ppr_discard_payment);

        editTextYearOfPayment = findViewById(R.id.edit_text_ppr_year_of_payment);
        editTextMonthOfYear = findViewById(R.id.edit_text_ppr_month_of_year);
        editTextDateOfMonth = findViewById(R.id.edit_text_ppr_date_of_month);
        editTextDayOfWeek = findViewById(R.id.edit_text_ppr_day_of_week);
        editTextAmountPaidReceived = findViewById(R.id.edit_text_ppr_amount_paid_received);
        editTextPersonPaid = findViewById(R.id.edit_text_ppr_person_paid);
        editTextPersonReceived = findViewById(R.id.edit_text_ppr_person_received);

        layoutPreparing = findViewById(R.id.layout_ppr_preparing);
        layoutSaveDiscard = findViewById(R.id.layout_ppr_save_discard);

        scrollViewInputs = findViewById(R.id.scroll_view_ppr_inputs);

        valid = false;
    }

    private void setClickListeners() {
        buttonSavePayment.setOnClickListener(this);
        buttonDiscardPayment.setOnClickListener(this);
    }

    private void getEditTexts() {
        mYearOfPayment = editTextYearOfPayment.getText().toString().trim();
        mMonthOfYear = editTextMonthOfYear.getText().toString().trim();
        mDateOfMonth = editTextDateOfMonth.getText().toString().trim();
        mDayOfWeek = editTextDayOfWeek.getText().toString().trim();
        mAmountPaidReceived = editTextAmountPaidReceived.getText().toString().trim();
        mPersonPaid = editTextPersonPaid.getText().toString().trim();
        mPersonReceived = editTextPersonReceived.getText().toString().trim();
    }

    private boolean validateForm() {
        if (!mYearOfPayment.isEmpty()) {
            if (mMonthOfYear.isEmpty()) {
                Toast.makeText(this, "Month of payment cannot be empty!", Toast.LENGTH_SHORT).show();
                editTextMonthOfYear.requestFocus();
            }
        } else {
            Toast.makeText(this, "Year of payment cannot be empty!", Toast.LENGTH_SHORT).show();
            editTextYearOfPayment.requestFocus();
        }

        return !mYearOfPayment.isEmpty() && !mMonthOfYear.isEmpty();
    }

    private void readSelfPayments() {
        final DatabaseReference referenceToSelfPayments = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(FIREBASE_RENT_PAID_RECEIVED)
                .child(mUid)
                .child(mORUid)
                .child(FIREBASE_OWNER_RENTER_ALL_PAYMENT);

        referenceToSelfPayments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    noOfMyEntry = dataSnapshot.getChildrenCount();
                    readPartneredPayments();
                } else {
                    Toast.makeText(AddPaymentPRActivity.this, "Data not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddPaymentPRActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void readPartneredPayments() {
        final DatabaseReference referenceToPartneredPayments = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(FIREBASE_RENT_PAID_RECEIVED)
                .child(mORUid)
                .child(mUid)
                .child(FIREBASE_OWNER_RENTER_ALL_PAYMENT);

        referenceToPartneredPayments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    noOfHisEntry = dataSnapshot.getChildrenCount();

                    layoutPreparing.setVisibility(View.GONE);
                    layoutSaveDiscard.setVisibility(View.VISIBLE);
                    scrollViewInputs.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(AddPaymentPRActivity.this, "Data not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddPaymentPRActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToFirebase() {
        DatabaseReference referenceToPayment = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(FIREBASE_RENT_PAID_RECEIVED)
                .child(mUid)
                .child(mORUid)
                .child(FIREBASE_OWNER_RENTER_ALL_PAYMENT);

        String paymentId = referenceToPayment.push().getKey();
        String fullDate = String.format("%s, %s/%s/%s", mDayOfWeek, mDateOfMonth, mMonthOfYear, mYearOfPayment);
        RentPaidReceived payment = new RentPaidReceived(
                fullDate,
                mAmountPaidReceived,
                mPersonPaid,
                mPersonReceived,
                paymentId
        );

        assert  paymentId != null;
        referenceToPayment.child(paymentId).setValue(payment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddPaymentPRActivity.this, "Transaction saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddPaymentPRActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_ppr_save_payment:
                getEditTexts();

                valid = validateForm();

                if (valid)
                    saveToFirebase();

                break;

            case R.id.button_ppr_discard_payment:
                Toast.makeText(this, "You discarded to save", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
