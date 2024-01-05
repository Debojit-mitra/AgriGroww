package com.dmsskbm.agrigroww.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;

public class SentEmailActivity extends AppCompatActivity {

    TextView usersName_email, user_email;
    EditText email_subjectEdittext, email_titleEdittext, email_bodyEdittext;
    Button emailPreview_button, button_send;
    String subject, title, body, name, email, uid, sentTo, emailTo, titleSaved, subjectSaved, bodySaved;
    LottieAnimationView custom_animationView;
    SharedPreferences Saved;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_email);

        usersName_email = findViewById(R.id.usersName_email);
        user_email = findViewById(R.id.user_email);
        email_subjectEdittext = findViewById(R.id.email_subjectEdittext);
        email_titleEdittext = findViewById(R.id.email_titleEdittext);
        email_bodyEdittext = findViewById(R.id.email_bodyEdittext);
        emailPreview_button = findViewById(R.id.emailPreview_button);
        button_send = findViewById(R.id.button_send);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        uid = getIntent().getStringExtra("uid");

        sentTo = "Email will be send to "+name;
        usersName_email.setText(sentTo);
        emailTo = "User`s Email: "+email;
        user_email.setText(emailTo);


        emailPreview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subject = email_subjectEdittext.getText().toString().trim();
                title = email_titleEdittext.getText().toString().trim();
                body = email_bodyEdittext.getText().toString();

                if(subject.isEmpty() || title.isEmpty() || body.isEmpty()){
                    Toast.makeText(SentEmailActivity.this, "Write in all the necessary fields!", Toast.LENGTH_SHORT).show();
                }else{
                    email_subjectEdittext.clearFocus();
                    email_titleEdittext.clearFocus();
                    email_bodyEdittext.clearFocus();

                    Intent intent = new Intent(SentEmailActivity.this, WebViewActivity.class);
                    intent.putExtra("whereFrom", "SentEmailActivity");
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("subject", subject);
                    intent.putExtra("title", title);
                    intent.putExtra("body", body);
                    startActivity(intent);

                }
            }
        });


        //load saved title, subject, body if any
        Saved = getSharedPreferences(uid, Context.MODE_PRIVATE);
        email_titleEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Saved.edit().putString("title",charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        titleSaved = Saved.getString("title", "");

        if(!titleSaved.isEmpty()){
            email_titleEdittext.setText(titleSaved);
        }

        email_subjectEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Saved.edit().putString("subject",charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        subjectSaved = Saved.getString("subject", "");

        if(!subjectSaved.isEmpty()){
            email_subjectEdittext.setText(subjectSaved);
        }

        email_bodyEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Saved.edit().putString("body",charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bodySaved = Saved.getString("body", "");

        if(!bodySaved.isEmpty()){
            email_bodyEdittext.setText(bodySaved);
        }

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subject = email_subjectEdittext.getText().toString().trim();
                title = email_titleEdittext.getText().toString().trim();
                body = email_bodyEdittext.getText().toString();

                if(subject.isEmpty() || title.isEmpty() || body.isEmpty()){

                    Toast.makeText(SentEmailActivity.this, "Write in all the necessary fields!", Toast.LENGTH_SHORT).show();

                }else {

                    AlertDialog.Builder sentEmailConfirm = new AlertDialog.Builder(SentEmailActivity.this, R.style.RoundedCornersDialog);
                    View viewSentEmail = LayoutInflater.from(SentEmailActivity.this).inflate(R.layout.custom_alert_dialog, null);
                    Button ok_button = viewSentEmail.findViewById(R.id.ok_button);
                    Button cancel_button = viewSentEmail.findViewById(R.id.cancel_button);
                    cancel_button.setVisibility(View.VISIBLE);
                    ok_button.setVisibility(View.VISIBLE);
                    custom_animationView = viewSentEmail.findViewById(R.id.custom_animationView);
                    custom_animationView.setAnimation("green_tick.json");
                    custom_animationView.setVisibility(View.VISIBLE);
                    //custom_animationView.setRepeatCount(LottieDrawable.);

                    int width = 800;
                    int height = 600;
                    RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                    parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    custom_animationView.setLayoutParams(parms);

                    TextView custom_textview = viewSentEmail.findViewById(R.id.custom_textview);
                    String custom_text = getString(R.string.confirm_email)+" "+name;
                    custom_textview.setText(custom_text);
                    custom_textview.setVisibility(View.VISIBLE);

                    sentEmailConfirm.setView(viewSentEmail);
                    AlertDialog alertDialog = sentEmailConfirm.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    ok_button.setTextColor(getResources().getColor(R.color.main_color));
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sentEmail(name, email, subject, title, body);
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            }
        });


      /*  AlertDialog.Builder confirm = new AlertDialog.Builder(SentEmailActivity.this, R.style.RoundedCornersDialog);
        View viewForAlert = LayoutInflater.from(SentEmailActivity.this).inflate(R.layout.custom_alert_dialog, null);
        custom_animationView = viewForAlert.findViewById(R.id.custom_animationView);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                custom_animationView.setAnimation("Loading_white.json");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                custom_animationView.setAnimation("Loading_black.json");
                break;
        }
        custom_animationView.setVisibility(View.VISIBLE);
        custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
        int width = 300;
        int height = 300;
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
        parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
        custom_animationView.setLayoutParams(parms);
        confirm.setView(viewForAlert);
        AlertDialog alertDialog = confirm.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.getWindow().setLayout(400, 400);
*/


    }

    private void sentEmail(String name, String email, String subject, String title, String body) {

        MaildroidX.Builder maildroidX = new MaildroidX.Builder();
        maildroidX.smtp("smtp-relay.sendinblue.com");
        maildroidX.smtpUsername("debojit16mitra@gmail.com");
        maildroidX.smtpPassword("19x7bQYZaUzyTNqX");
        maildroidX.port("465");
        maildroidX.type(MaildroidXType.HTML);
        maildroidX.to(email);
        maildroidX.from("agrigroww@gmail.com");
        maildroidX.subject(subject);

        maildroidX.body("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>" + subject + "</title>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                "  <table role=\"presentation\"\n" +
                "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: centre;\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td style=\"padding: 40px 0px 0px;\">\n" +
                "                  <div style=\"text-align: left;\">\n" +
                "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white; background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 80%;\"></div>\n" +
                "                  </div>\n" +
                "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">" + title + "</h1>\n" +
                "                       <p style=\"padding-bottom: 16px;\">" + body + "</p>" +
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
                "\n" +
                "</html>");
        maildroidX.isJavascriptDisabled(false);
        maildroidX.onCompleteCallback(new MaildroidX.onCompleteCallback() {
            @Override
            public void onSuccess() {
                onBackPressed();
                Saved.edit().clear().apply();
                Toast.makeText(SentEmailActivity.this, "Email successfully delivered to " + name + ".", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(@NonNull String s) {
                Toast.makeText(SentEmailActivity.this, "Failed to deliver email!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public long getTimeout() {
                return 0;
            }
        });
        maildroidX.mail();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}