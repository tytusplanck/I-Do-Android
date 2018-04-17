package com.example.tyle.ido;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * All original code.
 *
 * Contributors: Tytus and Kyle
 *
 * This class contains all of the methods for implementing 2 factor authentication.  The UI is instantiated, user inputs phone number and presses start,
 * then a verificationID and code are combined into a credential to log in on the server.  The phone number is sent to the server, and then the 6 digit code
 * is sent to the user in a text message.  Upon entering the code, the code is validated against the credential and if correct, the user is fully signed in.  
 */

public class SMSVerify extends AppCompatActivity implements View.OnClickListener {

    // Tag for logging events
    private static final String TAG = "SMSVERIFY";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    // Int numbers for progress states
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;

    // Instance of Firebase authentication
    private FirebaseAuth mAuth;

    // Variables for Verification
    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    // PhoneAuth variables for resending tokens, callback change
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    // Views that appear based on verification progress
    private ViewGroup mPhoneNumberViews;

    private TextView mDetailText;
    private TextView mUserField;

    //Fields for entering phone number and verification code
    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    // Buttons on the page
    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mCancelButton;
    private Encryption encrypter;

    //Info passed from login or registration
    String user_id, user_name, user_email, password;
    String KEYFORENCRYPTION = "This is the Key I guess";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verify);
        encrypter = new Encryption(KEYFORENCRYPTION);

        // Restore instance state
        if (savedInstanceState != null) { onRestoreInstanceState(savedInstanceState); }

        Bundle extras = getIntent().getExtras();
        user_id = encrypter.decryptText(extras.getByteArray("id"), KEYFORENCRYPTION);
        user_name = encrypter.decryptText(extras.getByteArray("username"), KEYFORENCRYPTION);
        user_email = encrypter.decryptText(extras.getByteArray("email"), KEYFORENCRYPTION);
        password = encrypter.decryptText(extras.getByteArray("password"), KEYFORENCRYPTION);

        Log.d(TAG, "User id up in this bitch: " + user_id);
        Log.d(TAG, "User name up in this bitch: " + user_name);
        Log.d(TAG, "User email up in this bitch: " + user_email);


        // Assign views on the page
        mPhoneNumberViews = findViewById(R.id.phone_auth_fields);
        mDetailText = findViewById(R.id.detail);
        mUserField = findViewById(R.id.user_field);
        mPhoneNumberField = findViewById(R.id.field_phone_number);
        mVerificationField = findViewById(R.id.field_verification_code);
        mStartButton = findViewById(R.id.button_start_verification);
        mVerifyButton = findViewById(R.id.button_verify_phone);
        mResendButton = findViewById(R.id.button_resend);
        mCancelButton = findViewById(R.id.button_cancel);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize phone auth callbacks
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
                updateUI(STATE_VERIFY_FAILED);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // Update UI
                updateUI(STATE_CODE_SENT);

            }};
    }

    @Override
    public void onStart() {
        super.onStart();
        // Load the initial SMS verification screen
        updateUI();
        Log.d(TAG, "SMSVERIFY STARTED");
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // Get an instance of the PhoneAuthProvider and start the process of verifying the phone number
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential verify = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(verify);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

   private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        final FirebaseUser current = mAuth.getCurrentUser();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            mAuth.signOut();
                            Log.d(TAG, "Signed out of accounts");
                            signInWithUserCreds();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mVerificationField.setError("Invalid code.");

                            }
                        }
                    }});
    }

    public void signInWithUserCreds(){
        mAuth.signInWithEmailAndPassword(user_email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        updateUI(STATE_VERIFY_SUCCESS, mAuth.getCurrentUser());
                    }
                });
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser());
    }

    private void updateUI() {
            updateUI(STATE_INITIALIZED, null);
    }
    private void updateUI(int uiState, FirebaseUser user) {
        if (user == null) {
            // Signed out
            mPhoneNumberViews.setVisibility(View.VISIBLE);

            mUserField.setText(user_email);
        }
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
                disableViews(mStartButton);
                mDetailText.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                Intent i = new Intent(SMSVerify.this, MainActivity.class);
                try {
                    i.putExtra("username", encrypter.encryptText(user_name, KEYFORENCRYPTION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    i.putExtra("id", encrypter.encryptText(user_id, KEYFORENCRYPTION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    i.putExtra("email", encrypter.encryptText(user_email, KEYFORENCRYPTION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(i);
                break;
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("No phone number entered.");
            return false;
        }
        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;

            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Please enter a verification code.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;

            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;

            case R.id.button_cancel:
                returnToHome();
        }
    }

    private void returnToHome() {
        mAuth.signOut();
        Toast.makeText(SMSVerify.this, "Cancelled Verification.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
