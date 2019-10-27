package com.bishram.payment.keeper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bishram.payment.keeper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.bishram.payment.keeper.Constants.COUNTRY_CODE_INDIA;
import static com.bishram.payment.keeper.Constants.REQUEST_TIME_OUT;

public class PayKeeperLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonEditMobileNo;
    private Button buttonClearVCode;
    private Button buttonSendVerificationCode;
    private Button buttonVerifyAndContinue;

    private EditText editTextMobileNo;
    private EditText editTextVerificationCode;

    private FirebaseAuth firebaseAuth;

    private String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_keeper_login);

        setTitle("Sign Into Your Account");

        // Initialize view objects
        initializeViews();

        // Initialization of firebase objects
        initializeFirebase();

        registerButtonClickListeners();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login_edit_mobile_no:
                Toast.makeText(this, "This is editing", Toast.LENGTH_SHORT).show();
                editTextMobileNo.setEnabled(true);
                editTextMobileNo.selectAll();
                editTextVerificationCode.setEnabled(false);
                buttonEditMobileNo.setEnabled(false);
                buttonClearVCode.setEnabled(false);
                buttonVerifyAndContinue.setEnabled(false);
                buttonSendVerificationCode.setEnabled(true);
                buttonSendVerificationCode.setText(getString(R.string.btn_txt_send_verification_code));
                break;

            case R.id.button_login_clear_code:
                Toast.makeText(this, "This is clearing", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_login_send_verification_code:
                initiateVerification();
                break;

            case R.id.button_login_verify_and_continue:
                checkVerificationCode();
                Toast.makeText(this, "Verifying and continuing", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void initializeViews() {
        buttonEditMobileNo = findViewById(R.id.button_login_edit_mobile_no);
        buttonClearVCode = findViewById(R.id.button_login_clear_code);
        buttonSendVerificationCode = findViewById(R.id.button_login_send_verification_code);
        buttonVerifyAndContinue = findViewById(R.id.button_login_verify_and_continue);

        editTextMobileNo = findViewById(R.id.edit_text_login_10_digit_mobile_no);
        editTextVerificationCode = findViewById(R.id.edit_text_login_6_digit_verification_code);
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void registerButtonClickListeners() {
        buttonEditMobileNo.setOnClickListener(this);
        buttonClearVCode.setOnClickListener(this);
        buttonSendVerificationCode.setOnClickListener(this);
        buttonVerifyAndContinue.setOnClickListener(this);
    }

    // Verification of mobile will start from here
    private void initiateVerification() {
        String etMobileNumber = editTextMobileNo.getText().toString().trim();

        if (etMobileNumber.length() != 0) {
            etMobileNumber = String.format("%s%s", COUNTRY_CODE_INDIA, etMobileNumber);

            if (etMobileNumber.length() == 13) {
                // EditText "10 Digit Mobile Number"
                editTextMobileNo.setEnabled(false);

                // Button "Edit"
                buttonEditMobileNo.setEnabled(false);

                // Button "SEND VERIFICATION CODE"
                buttonSendVerificationCode.setEnabled(false);
                buttonSendVerificationCode.setText(String
                        .format("Verification code sent to %s", etMobileNumber.substring(3)));
                buttonSendVerificationCode.setTextColor(Color.WHITE);

                // EditText "6 Digit verification code"
                editTextVerificationCode.setEnabled(false);

                // Button "Clear"
                buttonClearVCode.setEnabled(false);

                // Button "VERIFY AND CONTINUE"
                buttonVerifyAndContinue.setEnabled(false);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        etMobileNumber,
                        REQUEST_TIME_OUT,
                        TimeUnit.SECONDS,
                        TaskExecutors.MAIN_THREAD,
                        verificationStateChangedCallbacks
                );
            } else {
                Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Mobile number cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkVerificationCode() {
        String verificationCode = editTextVerificationCode.getText().toString().toLowerCase();

        if (verificationCode.length() != 0) {
            if (verificationCode.length() == 6) {
                editTextMobileNo.setEnabled(false);
                buttonEditMobileNo.setEnabled(false);
                buttonSendVerificationCode.setEnabled(false);
                editTextVerificationCode.setEnabled(false);
                buttonClearVCode.setEnabled(false);
                buttonVerifyAndContinue.setEnabled(false);

                generateCredential(verificationCode);
            } else {
                Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Verification code empty", Toast.LENGTH_SHORT).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // EditText "10 Digit Mobile Number"
            // editTextMobileNo.setEnabled(false);

            // Button "Edit"
            buttonEditMobileNo.setEnabled(true);

            // Button "SEND VERIFICATION CODE"
            buttonSendVerificationCode.setEnabled(false);

            // EditText "6 Digit verification code"
            editTextVerificationCode.setEnabled(true);

            // Button "Clear"
            buttonClearVCode.setEnabled(true);

            // Button "VERIFY AND CONTINUE"
            buttonVerifyAndContinue.setEnabled(true);

            // Get the verificationCode via messaging service
            String verificationCodeReceived = phoneAuthCredential.getSmsCode();

            // Sometimes the code is not detected automatically
            // In this the code will be null and
            // User has to enter the code manually
            if (verificationCodeReceived != null) {
                // Code is detected by the app
                // Hence set the code to verification text field
                editTextVerificationCode.setText(verificationCodeReceived);

                // And also generate a verification credential to confirm
                // that code is delivered to the right person
                generateCredential(verificationCodeReceived);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException firebaseException) {
            Toast.makeText(PayKeeperLoginActivity.this, firebaseException.getMessage(), Toast.LENGTH_LONG).show();

            editTextMobileNo.setEnabled(true);
            editTextMobileNo.selectAll();
            editTextVerificationCode.setEnabled(false);
            buttonEditMobileNo.setEnabled(false);
            buttonClearVCode.setEnabled(false);
            buttonVerifyAndContinue.setEnabled(false);
            buttonSendVerificationCode.setEnabled(true);
            buttonSendVerificationCode.setText(getString(R.string.btn_txt_send_verification_code));
        }

        @Override
        public void onCodeSent(@NonNull String vidReceived, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(vidReceived, forceResendingToken);
            Toast.makeText(PayKeeperLoginActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();
            // Store the verification ID sent to the provided mobile number
            verificationID = vidReceived;

            // Button "Edit"
            buttonEditMobileNo.setEnabled(true);

            // Button "SEND VERIFICATION CODE"
            buttonSendVerificationCode.setEnabled(false);

            // EditText "6 Digit verification code"
            editTextVerificationCode.setEnabled(true);

            // Button "Clear"
            buttonClearVCode.setEnabled(true);

            // Button "VERIFY AND CONTINUE"
            buttonVerifyAndContinue.setEnabled(true);
        }
    };

    private void generateCredential(String verificationCode) {
        // Generate authentication credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, verificationCode);

        // Sign in with the credential just generated
        signInWithPhoneCredential(credential);
    }

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // If credentials matched goto main activity
                    startActivity(new Intent(PayKeeperLoginActivity.this, PayKeeperMainActivity.class));

                    // And finish the current activity
                    finish();
                } else {
                    Toast.makeText(PayKeeperLoginActivity.this,
                            Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
