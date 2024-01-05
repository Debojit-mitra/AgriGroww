package com.dmsskbm.agrigroww;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;

public class EmailVerificationActivity extends AppCompatActivity {

    String randomCapString, emailString, emailTextview, email, intentExtra;
    Button button_verify_otp, button_continue_otp;
    EditText edittext_email_signup,edittext_email_otp_signup;
    TextView textView_email_otp_Sent, textView_email_otp_verify, skip_email;
    ImageButton back_button;
    CardView card_back_button;
    TextInputLayout textview_email_otp_signup;
    ProgressBar progressBar;
    FirebaseAuth authProfile = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);


        textView_email_otp_Sent = findViewById(R.id.textView_email_otp_Sent);
        button_verify_otp = findViewById(R.id.button_verify_otp);
        button_continue_otp = findViewById(R.id.button_continue_otp);
        edittext_email_signup = findViewById(R.id.edittext_email_signup);
        card_back_button = findViewById(R.id.card_back_button);
        skip_email = findViewById(R.id.skip_email);
        back_button = findViewById(R.id.back_button);
        textView_email_otp_verify = findViewById(R.id.textView_email_otp_verify);
        textview_email_otp_signup = findViewById(R.id.textview_email_otp_signup);
        edittext_email_otp_signup = findViewById(R.id.edittext_email_otp_signup);
        progressBar = findViewById(R.id.progressBar);

        try{
            intentExtra = getIntent().getStringExtra("extra");
            if(!intentExtra.isEmpty()){
                String newEmailTextview = "Please enter your new email address";
                textView_email_otp_verify.setText(newEmailTextview);
            }

            if(intentExtra.equals("fromEditProfile")){
                card_back_button.setVisibility(View.VISIBLE);
            } else if(intentExtra.equals("fromRegistration")){
                skip_email.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            Log.e("intentException", e.getMessage());
        }

        skip_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                finish();
            }
        });


        button_continue_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edittext_email_signup.getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(EmailVerificationActivity.this, "Please enter valid email!", Toast.LENGTH_LONG).show();
                } else if(email.isEmpty()){
                    Toast.makeText(EmailVerificationActivity.this, "Please enter email!", Toast.LENGTH_LONG).show();
                }else{
                    button_continue_otp.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    randomCapString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                    StringBuilder randomString = new StringBuilder();
                    Random rnd = new Random();
                    while (randomString.length() < 6) { // length of the random string.
                        int index = (int) (rnd.nextFloat() * randomCapString.length());
                        randomString.append(randomCapString.charAt(index));
                    }
                    emailString = randomString.toString();

                    MaildroidX.Builder maildroidXUser = new MaildroidX.Builder();
                    maildroidXUser.smtp("smtp-relay.sendinblue.com");
                    maildroidXUser.smtpUsername("debojit16mitra@gmail.com");
                    maildroidXUser.smtpPassword("19x7bQYZaUzyTNqX");
                    maildroidXUser.port("587");
                    maildroidXUser.type(MaildroidXType.HTML);
                    maildroidXUser.to(email);
                    maildroidXUser.from("agrigroww@gmail.com");
                    maildroidXUser.subject("OTP FOR VERIFICATION OF YOUR EMAIL");
                    maildroidXUser.body("<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                            "  <title>Verify your login</title>\n" +
                            "</head>\n" +
                            "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                            "  <table role=\"presentation\"\n" +
                            "    style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                            "    <tbody>\n" +
                            "      <tr>\n" +
                            "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                            "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n" +
                            "            <tbody>\n" +
                            "              <tr>\n" +
                            "                <td style=\"padding: 40px 0px 0px;\">\n" +
                            "                  <div style=\"text-align: left;\">\n" +
                            "                    <div style=\"margin-bottom: -50px; text-align: center;background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"Company\" style=\"width: 256px;\"></div>\n" +
                            "                  </div>\n" +
                            "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                            "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                            "                      <h1 style=\"margin: 1rem 0\">Verification code</h1>\n" +
                            "                      <p style=\"padding-bottom: 16px\">Please use the verification code below to verify your email.</p>\n" +
                            "                      <p style=\"padding-bottom: 16px\"><strong style=\"font-size: 130%\">"+emailString+"</strong></p>\n" +
                            "                      <p style=\"padding-bottom: 16px\">If you didn’t request this, you can ignore this email.</p>\n" +
                            "                      <p style=\"padding-bottom: 16px\">Thanks,<br>The AgriGroww team</p>\n" +
                            "                    </div>\n" +
                            "                  </div>\n" +
                            "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n" +
                            "                    <p style=\"padding-bottom: 16px\">Made with \uD83D\uDC96 in India</p>\n" +
                            "                  </div>\n" +
                            "                </td>\n" +
                            "              </tr>\n" +
                            "            </tbody>\n" +
                            "          </table>\n" +
                            "        </td>\n" +
                            "      </tr>\n" +
                            "    </tbody>\n" +
                            "  </table>\n" +
                            "</body>\n" +
                            "</html>");
                    maildroidXUser.isJavascriptDisabled(false);
                    maildroidXUser.onCompleteCallback(new MaildroidX.onCompleteCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EmailVerificationActivity.this, "OTP has been mailed to you!", Toast.LENGTH_SHORT).show();
                            edittext_email_signup.setEnabled(false);
                            textview_email_otp_signup.setVisibility(View.VISIBLE);
                            emailTextview  = "OTP will be sent to " + email;
                            progressBar.setVisibility(View.GONE);
                            button_verify_otp.setVisibility(View.VISIBLE);
                            textView_email_otp_Sent.setText(emailTextview);
                            textView_email_otp_Sent.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFail(@NonNull String s) {
                            progressBar.setVisibility(View.GONE);
                            button_continue_otp.setVisibility(View.VISIBLE);
                            Toast.makeText(EmailVerificationActivity.this, "OTP send failed!", Toast.LENGTH_SHORT).show();
                            Log.e("OTP Sent Fail", s);
                        }

                        @Override
                        public long getTimeout() {
                            return 0;
                        }
                    });
                    maildroidXUser.mail();
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

            button_verify_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userString = edittext_email_otp_signup.getText().toString();

                    if(userString.isEmpty()){
                        Toast.makeText(EmailVerificationActivity.this, "Please Enter OTP!", Toast.LENGTH_SHORT).show();
                    }else if(userString.length() != 6){
                        Toast.makeText(EmailVerificationActivity.this, "Please Enter Complete OTP!", Toast.LENGTH_SHORT).show();
                    }else if(!userString.equals(emailString)){
                        Toast.makeText(EmailVerificationActivity.this, "Please Enter Valid OTP!", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBar.setVisibility(View.VISIBLE);
                        button_verify_otp.setVisibility(View.GONE);
                        edittext_email_otp_signup.clearFocus();
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
                        referenceProfile.child(authProfile.getCurrentUser().getUid()).child("email").setValue(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.setVisibility(View.GONE);
                                button_verify_otp.setVisibility(View.VISIBLE);
                                Toast.makeText(EmailVerificationActivity.this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                button_verify_otp.setVisibility(View.VISIBLE);
                                Toast.makeText(EmailVerificationActivity.this, "Email verification failed!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }

        /*textView_email_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmailVerificationActivity.this, EditProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                finish();
            }
        });*/




    }
