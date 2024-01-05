package com.dmsskbm.agrigroww.expert;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    TextView textview_change_language;
    EditText edittext_number_login;
    Button button_send_otp;
    ProgressBar progressBar;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isOnline()) {

            authProfile = FirebaseAuth.getInstance();
            //get instance of the current user
            firebaseUser = authProfile.getCurrentUser();

        edittext_number_login = findViewById(R.id.edittext_number_login);
        button_send_otp = findViewById(R.id.button_send_otp);
        progressBar = findViewById(R.id.progressBar);
        textview_change_language = findViewById(R.id.textview_change_language);

        //show keyboard
        edittext_number_login.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            try{ //phone number hint
                GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();

                ActivityResultLauncher<IntentSenderRequest> phoneNumberHintIntentResultLauncher =
                        registerForActivityResult(
                                new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                                    @Override
                                    public void onActivityResult(ActivityResult result) {
                                        try {
                                            String phoneNumber = Identity.getSignInClient(getApplicationContext()).getPhoneNumberFromIntent(result.getData());
                                            edittext_number_login.setText(phoneNumber.substring(3));
                                            //to put cursor at end of edittext
                                            edittext_number_login.setSelection(edittext_number_login.getText().length());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                Identity.getSignInClient(LoginActivity.this)
                        .getPhoneNumberHintIntent(request)
                        .addOnSuccessListener(result -> {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        IntentSender intentSender = result.getIntentSender();
                                        phoneNumberHintIntentResultLauncher.launch(new IntentSenderRequest.Builder(intentSender).build());
                                    } catch (Exception e) {
                                        Log.i("Error launching", "error occurred in launching Activity result");
                                    }
                                }
                            }, 1500);

                        })
                        .addOnFailureListener(e -> Log.i("Failure occurred", "Failure getting phone number"));
            } catch (Exception e){
                Log.e("Phone Hint Error", e.getMessage());
            }

        button_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide keyboard
                edittext_number_login.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittext_number_login.getWindowToken(), 0);

                String storeNumber = edittext_number_login.getText().toString().trim();
                if (!storeNumber.isEmpty()) {
                    if (storeNumber.length() == 10) {

                        progressBar.setVisibility(View.VISIBLE);
                        button_send_otp.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


                        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                button_send_otp.setVisibility(View.VISIBLE);

                                Toast.makeText(getApplicationContext(), "Verification Completed!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                button_send_otp.setVisibility(View.VISIBLE);
                                edittext_number_login.requestFocus();
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("onVerficationFailed", e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String otpsent, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                button_send_otp.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                                intent.putExtra("mobile", storeNumber);
                                intent.putExtra("otpsent", otpsent);
                                startActivity(intent);
                            }
                        };

                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(authProfile)
                                        .setPhoneNumber("+91" + storeNumber)       // Phone number to verify
                                        .setTimeout(0L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);

                    } else {
                        Toast.makeText(LoginActivity.this, "Enter a valid number!", Toast.LENGTH_SHORT).show();
                        edittext_number_login.requestFocus();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Enter Phone Number To Continue!", Toast.LENGTH_SHORT).show();
                    edittext_number_login.requestFocus();
                }
            }
        });

    } else {
            Intent intent = new Intent(LoginActivity.this, ErrorActivity.class);
            intent.putExtra("ExtraText","No Internet Connection!");
            startActivity(intent);
            finish();
        }
        textview_change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent intent = new Intent(LoginActivity.this, ChangeLanguageActivity.class);
              //  startActivity(intent);
            }
        });

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}