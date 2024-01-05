package com.dmsskbm.agrigroww;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hadi.emojiratingbar.EmojiRatingBar;
import com.hadi.emojiratingbar.RateStatus;

import java.util.Locale;
import java.util.Objects;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;

public class FeedbackActivity extends AppCompatActivity {


    EmojiRatingBar emoji_rating_bar;
    Button button_submit_feedback;
    EditText edittext_feedback_description;
    ImageView feedback_image;
    ImageButton back_button;
    CheckBox checkbox_get_copy_feedback;
    String rateString, desc;
    String oldUri;
    Uri uriImage;
    ProgressBar progressBar;
    FirebaseAuth authProfile;
    private String fullName, uniqueId, phone, email;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (isOnline()) {

            back_button = findViewById(R.id.back_button);
            authProfile = FirebaseAuth.getInstance();

            emoji_rating_bar = findViewById(R.id.emoji_rating_bar);
            emoji_rating_bar.setAwfulEmojiTitle(getString(R.string.poor_feedback));
            emoji_rating_bar.setBadEmojiTitle(getString(R.string.bad_feedback));
            emoji_rating_bar.setOkayEmojiTitle(getString(R.string.okay_feedback));
            emoji_rating_bar.setGoodEmojiTitle(getString(R.string.good_feedback));
            emoji_rating_bar.setGreatEmojiTitle(getString(R.string.great_feedback));


            button_submit_feedback = findViewById(R.id.button_submit_feedback);
            edittext_feedback_description = findViewById(R.id.edittext_feedback_description);
            feedback_image = findViewById(R.id.feedback_image);
            checkbox_get_copy_feedback = findViewById(R.id.checkbox_get_copy_feedback);
            progressBar = findViewById(R.id.progressBar);

            feedback_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageChoser();
                }
            });

            getProfile();


            button_submit_feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (emoji_rating_bar.getCurrentRateStatus() == RateStatus.EMPTY) {
                        Toast.makeText(FeedbackActivity.this, "Please Rate This App First!", Toast.LENGTH_SHORT).show();
                    } else {
                        edittext_feedback_description.clearFocus();
                        progressBar.setVisibility(View.VISIBLE);
                        button_submit_feedback.setVisibility(View.GONE);

                        if (emoji_rating_bar.getCurrentRateStatus() == RateStatus.AWFUL) {
                            rateString = "POOR";
                        } else if (emoji_rating_bar.getCurrentRateStatus() == RateStatus.BAD) {
                            rateString = "BAD";
                        } else if (emoji_rating_bar.getCurrentRateStatus() == RateStatus.OKAY) {
                            rateString = "OKAY";
                        } else if (emoji_rating_bar.getCurrentRateStatus() == RateStatus.GOOD) {
                            rateString = "GOOD";
                        } else if (emoji_rating_bar.getCurrentRateStatus() == RateStatus.GREAT) {
                            rateString = "GREAT";
                        }

                        desc = edittext_feedback_description.getText().toString().trim();
                        if (desc.isEmpty()) {
                            desc = "n-a";
                        }

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int height = displayMetrics.heightPixels;
                        int width = displayMetrics.widthPixels;

                        try {

                            String uniqueId_ = "Unique Id: " + uniqueId;
                            String email_ = "Email: " + email;
                            String phone_ = "Phone No: " + phone;
                            String name_ = "Name: " + fullName;
                            String rating_ = "Rating by user: " + rateString;
                            String desc_ = "Rating Description: " + desc;

                            SharedPreferences options = getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                            String savedLanguage = options.getString("SelectedLanguage", "English");


                            String device_ = "Device: " + Build.DEVICE;
                            String sdk_ = "SDK Version: " + Build.VERSION.SDK_INT;
                            String appV_ = "App Version: " + BuildConfig.VERSION_CODE;
                            String appL_ = "App Language: " + savedLanguage;
                            String systemL_ = "System Language: " + Resources.getSystem().getConfiguration().locale.getLanguage();
                            String deviceM_ = "Device Model: " + Build.MODEL;
                            String manufacturer_ = "Manufacture: " + Build.MANUFACTURER;
                            String brand_ = "Brand: " + Build.BRAND;
                            String hardware_ = "Hardware: " + Build.HARDWARE;
                            String versionC_ = "Version code: " + Build.VERSION.RELEASE;
                            String screenResolution = "Screen Resolution: " + height + " * " + width;

                            //String allSpecs = stringBuilder.toString();

                            MaildroidX.Builder maildroidX = new MaildroidX.Builder();
                            maildroidX.smtp("smtp-relay.sendinblue.com");
                            maildroidX.smtpUsername("debojit16mitra@gmail.com");
                            maildroidX.smtpPassword("19x7bQYZaUzyTNqX");
                            maildroidX.port("465");
                            maildroidX.type(MaildroidXType.HTML);
                            maildroidX.to("debojit16mitra@gmail.com");
                            maildroidX.from("agrigroww@gmail.com");
                            if (uriImage != null) {
                                String newUri = uriImage.toString().substring(7);
                                maildroidX.attachment(newUri);
                            }
                            maildroidX.subject("Feedback by Unique Id " + uniqueId);
                            maildroidX.body("<!DOCTYPE html>\n" +
                                    "<html>\n" +
                                    "\n" +
                                    "<head>\n" +
                                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                                    "  <title>Verify your login</title>\n" +
                                    "</head>\n" +
                                    "\n" +
                                    "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                                    "  <table role=\"presentation\"\n" +
                                    "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                                    "    <tbody>\n" +
                                    "      <tr>\n" +
                                    "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                                    "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n" +
                                    "            <tbody>\n" +
                                    "              <tr>\n" +
                                    "                <td style=\"padding: 40px 0px 0px;\">\n" +
                                    "                  <div style=\"text-align: left;\">\n" +
                                    "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white;background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 256px;\"></div>\n" +
                                    "                  </div>\n" +
                                    "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                                    "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                                    "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">Feedback from user</h1>\n" +
                                    "                      <p style=\"padding-bottom: 8px\"><strong style=\"font-size: 130%\">User Info:</strong></p>\n" +
                                    "                       <p style=\"padding-bottom: 16px; color:black;\">" + uniqueId_ + "<br>" + email_ + "<br>" + phone_ + "<br>" + name_ + "<br>" + rating_ + "<br>" + desc_ + "<br>" + "</p>" +
                                    "                      <p style=\"padding-bottom: 8px\"><strong style=\"font-size: 130%\">Device Info:</strong></p>\n" +
                                    "                       <p style=\"padding-bottom: 16px; color:black;\">" + device_ + "<br>" + sdk_ + "<br>" + appV_ + "<br>" + appL_ + "<br>" + systemL_ + "<br>" + deviceM_ + "<br>" + manufacturer_ + "<br>" + brand_ + "<br>" + hardware_ + "<br>" + versionC_ + "<br>" + screenResolution + "<br>" + "</p>\n" +
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
                                    "\n" +
                                    "</html>");
                            maildroidX.isJavascriptDisabled(false);
                            maildroidX.onCompleteCallback(new MaildroidX.onCompleteCallback() {
                                @Override
                                public void onSuccess() {
                                    progressBar.setVisibility(View.GONE);
                                    button_submit_feedback.setVisibility(View.VISIBLE);
                                    Toast.makeText(FeedbackActivity.this, "Feedback successfully delivered!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFail(@NonNull String s) {
                                    progressBar.setVisibility(View.GONE);
                                    button_submit_feedback.setVisibility(View.VISIBLE);
                                    Toast.makeText(FeedbackActivity.this, "Failed to deliver!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public long getTimeout() {
                                    return 0;
                                }
                            });
                            maildroidX.mail();

                            if (checkbox_get_copy_feedback.isChecked()) {
                                MaildroidX.Builder maildroidXUser = new MaildroidX.Builder();
                                maildroidXUser.smtp("smtp-relay.sendinblue.com");
                                maildroidXUser.smtpUsername("debojit16mitra@gmail.com");
                                maildroidXUser.smtpPassword("19x7bQYZaUzyTNqX");
                                maildroidXUser.port("587");
                                maildroidXUser.type(MaildroidXType.HTML);
                                maildroidXUser.to(email);
                                maildroidXUser.from("agrigroww@gmail.com");
                                if (uriImage != null) {
                                    String newUri = uriImage.toString().substring(7);
                                    maildroidX.attachment(newUri);
                                }
                                maildroidXUser.subject("Feedback by Unique Id " + uniqueId + " (COPY)");
                                maildroidXUser.body("<!DOCTYPE html>\n" +
                                        "<html>\n" +
                                        "\n" +
                                        "<head>\n" +
                                        "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                                        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                                        "  <title>Verify your login</title>\n" +
                                        "</head>\n" +
                                        "\n" +
                                        "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                                        "  <table role=\"presentation\"\n" +
                                        "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                                        "    <tbody>\n" +
                                        "      <tr>\n" +
                                        "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                                        "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n" +
                                        "            <tbody>\n" +
                                        "              <tr>\n" +
                                        "                <td style=\"padding: 40px 0px 0px;\">\n" +
                                        "                  <div style=\"text-align: left;\">\n" +
                                        "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white;background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 256px;\"></div>\n" +
                                        "                  </div>\n" +
                                        "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                                        "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                                        "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">Feedback from user (COPY)</h1>\n" +
                                        "                      <p style=\"padding-bottom: 8px\"><strong style=\"font-size: 130%\">User Info:</strong></p>\n" +
                                        "                       <p style=\"padding-bottom: 16px; color:black;\">" + uniqueId_ + "<br>" + email_ + "<br>" + phone_ + "<br>" + name_ + "<br>" + rating_ + "<br>" + desc_ + "<br>" + "</p>" +
                                        "                      <p style=\"padding-bottom: 8px\"><strong style=\"font-size: 130%\">Device Info:</strong></p>\n" +
                                        "                       <p style=\"padding-bottom: 16px; color:black;\">" + device_ + "<br>" + sdk_ + "<br>" + appV_ + "<br>" + appL_ + "<br>" + systemL_ + "<br>" + deviceM_ + "<br>" + manufacturer_ + "<br>" + brand_ + "<br>" + hardware_ + "<br>" + versionC_ + "<br>" + screenResolution + "<br>" + "</p>\n" +
                                        "                      <p style=\"padding-bottom: 8px;\">" + "Thanks you for the feedback,<br>The AgriGroww team"+"</p>\n" +
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
                                        "\n" +
                                        "</html>");



                                maildroidXUser.isJavascriptDisabled(false);
                                maildroidXUser.onCompleteCallback(new MaildroidX.onCompleteCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(FeedbackActivity.this, "Feedback copy also has been mailed to you!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFail(@NonNull String s) {
                                        Toast.makeText(FeedbackActivity.this, "Feedback copy send failed!", Toast.LENGTH_SHORT).show();
                                        Log.e("Feedback Sent Fail", s);
                                    }

                                    @Override
                                    public long getTimeout() {
                                        return 0;
                                    }
                                });
                                maildroidXUser.mail();
                            }
                        } catch (Exception e) {
                            Toast.makeText(FeedbackActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

        } else {
            Intent intent = new Intent(FeedbackActivity.this, ErrorActivity.class);
            intent.putExtra("ExtraText", "No Internet Connection!");
            startActivity(intent);
            finish();
        }

    }

    private void getProfile() {

        try {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
            referenceProfile.child(authProfile.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readWriteUserDetails != null) {

                        fullName = readWriteUserDetails.fullName;
                        uniqueId = readWriteUserDetails.userId;
                        phone = readWriteUserDetails.phone;
                        email = readWriteUserDetails.email;

                    } else {
                        Toast.makeText(FeedbackActivity.this, "Error getting user info!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FeedbackActivity.this, "Error getting user info!!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(FeedbackActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void openImageChoser() {
        ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .start(PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            uriImage = data.getData();
            oldUri = "oldImage";
            feedback_image.setImageURI(uriImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}