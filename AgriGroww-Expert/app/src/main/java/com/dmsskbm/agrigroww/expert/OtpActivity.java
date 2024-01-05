package com.dmsskbm.agrigroww.expert;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dmsskbm.agrigroww.expert.extras.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.otpview.OTPListener;
import com.otpview.OTPTextView;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpActivity extends AppCompatActivity {

    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;

    Button button_verify_otp;
    String otpsentverify;
    ProgressBar progressBar;
    TextView textresendotp;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    OTPTextView otpTextView;
    String textOtp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        progressBar = findViewById(R.id.progressBar);
        textresendotp = findViewById(R.id.textresendotp);
        otpTextView = findViewById(R.id.otpTextView);

        TextView textView_otp_Sent = findViewById(R.id.textView_otp_Sent);
        textView_otp_Sent.setText("OTP is sent to this number +91" + getIntent().getStringExtra("mobile"));

        otpsentverify = getIntent().getStringExtra("otpsent");

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        startSmartUserConsent();

        //show keyboard
       /* inputotp1.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);*/
        otpTextView.requestFocusOTP();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        button_verify_otp = findViewById(R.id.button_verify_otp);
        button_verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if any of the otp box is empty
                verifyOtp();
            }
        });

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(@NonNull String s) {
                verifyOtp();
            }
        });


        textresendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + getIntent().getStringExtra("mobile"), 60, TimeUnit.SECONDS, OtpActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                progressBar.setVisibility(View.GONE);
                                button_verify_otp.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                button_verify_otp.setVisibility(View.VISIBLE);
                                Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String otpsent, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                progressBar.setVisibility(View.GONE);
                                button_verify_otp.setVisibility(View.VISIBLE);
                                otpsentverify = otpsent;
                                Toast.makeText(OtpActivity.this, "Otp Resend Successful!!", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

    }

    private void startSmartUserConsent() {

        SmsRetrieverClient client = SmsRetriever.getClient(OtpActivity.this);
        client.startSmsUserConsent(null);

    }
    private void registerBroadcastReceiver(){

        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

                startActivityForResult(intent, REQ_USER_CONSENT);

            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_USER_CONSENT){
            if((resultCode == RESULT_OK) && (data != null)){
               String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpAndSetOtp(message);
            }
        }
    }

    private void getOtpAndSetOtp(String message) {

        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);

        try{
            if (matcher.find()) {
                otpTextView.setOTP(matcher.group(0));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    private void verifyOtp() {

        textOtp = otpTextView.getOtp();
        if (!textOtp.equals("")) {
            if (textOtp.length() == 6) {

                String checkingOtp = textOtp;

                if (otpsentverify != null) {
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(otpTextView.getWindowToken(), 0);
                    otpTextView.clearFocus();
                    progressBar.setVisibility(View.VISIBLE);
                    button_verify_otp.setVisibility(View.INVISIBLE);

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otpsentverify, checkingOtp);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                //check if new user or old user
                                FirebaseAuth authProfile = FirebaseAuth.getInstance();
                                FirebaseUser firebaseUser = authProfile.getCurrentUser();

                                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("AdminDetails");
                                referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Toast.makeText(OtpActivity.this, "This App is not for Admins! Please log in using Agrigroww Admin!", Toast.LENGTH_SHORT).show();
                                            try {
                                                authProfile.signOut();
                                                Intent intent = new Intent(OtpActivity.this, ErrorActivity.class);
                                                intent.putExtra("ExtraText", "This App is not for Admins! Please log in using Agrigroww Admin!");
                                                startActivity(intent);
                                                finish();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
                                            referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        Toast.makeText(OtpActivity.this, "This App is not for users! Please log in using Agrigroww from playstore!", Toast.LENGTH_SHORT).show();
                                                        try {
                                                            authProfile.signOut();
                                                            Intent intent = new Intent(OtpActivity.this, ErrorActivity.class);
                                                            intent.putExtra("ExtraText", "This App is not for users! Please log in using Agrigroww from playstore!");
                                                            startActivity(intent);
                                                            finish();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {

                                                        //check if new user or old user
                                                        boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                                        if (isNew) {
                                                            DatabaseReference newUserDetails = FirebaseDatabase.getInstance().getReference().child("userDetails");
                                                            newUserDetails.child(authProfile.getCurrentUser().getUid()).setValue("newuser").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    button_verify_otp.setVisibility(View.VISIBLE);
                                                                    Intent intent = new Intent(OtpActivity.this, RegistrationActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            });

                                                        } else {
                                                            DatabaseReference referenceCheckBanned = FirebaseDatabase.getInstance().getReference("banned").child("users");
                                                            referenceCheckBanned.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.hasChild(authProfile.getCurrentUser().getUid())) {
                                                                        progressBar.setVisibility(View.GONE);
                                                                        button_verify_otp.setVisibility(View.VISIBLE);
                                                                        authProfile.signOut();
                                                                        Intent intent = new Intent(OtpActivity.this, ErrorActivity.class);
                                                                        intent.putExtra("ExtraText", "userBanned");
                                                                        startActivity(intent);
                                                                        overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                                                                        finishAffinity();
                                                                    } else {
                                                                        progressBar.setVisibility(View.GONE);
                                                                        button_verify_otp.setVisibility(View.VISIBLE);
                                                                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                                                        startActivity(intent);
                                                                        overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                                                                        finishAffinity();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Log.e("DatabaseErrorOtp", error.getMessage());
                                                                    Toast.makeText(OtpActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                Toast.makeText(OtpActivity.this, "Enter valid OTP!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                button_verify_otp.setVisibility(View.VISIBLE);
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.toggleSoftInputFromWindow(otpTextView.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                                otpTextView.requestFocusOTP();

                            }
                        }
                    });


                } else {
                    Toast.makeText(OtpActivity.this, "VerifyOtp: Some Error Occurred!!", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(OtpActivity.this, "Enter all 6 digits!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}

