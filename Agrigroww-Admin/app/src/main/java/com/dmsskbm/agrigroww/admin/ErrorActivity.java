package com.dmsskbm.agrigroww.admin;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class ErrorActivity extends AppCompatActivity {

    TextView textView_error;
    Button refresh_btn;
    String getText, getTextError;
    LottieAnimationView error_animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        textView_error = findViewById(R.id.textView_error);
        refresh_btn = findViewById(R.id.refresh_btn);
        error_animationView =findViewById(R.id.error_animationView);

        try{
             getText = getIntent().getStringExtra("ExtraText");
             getTextError = getIntent().getStringExtra("Error");
        } catch (Exception e){
            e.printStackTrace();
        }


        if(getText.equals("No Internet Connection!")){
           // textView_error.setText(getText);
            error_animationView.setVisibility(View.VISIBLE);
            refresh_btn.setVisibility(View.VISIBLE);
            refresh_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(isOnline()) {
                        Intent intent = new Intent(ErrorActivity.this, Splash_Screen.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }
            });

        } else if (getText.equals("You have been signed out!")) {
            error_animationView.setAnimation("loggedout.json");
            error_animationView.setVisibility(View.VISIBLE);
            textView_error.setText(getText);
            String login = "Login";
            refresh_btn.setText(login);
            refresh_btn.setVisibility(View.VISIBLE);
            refresh_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline()){
                        Intent intent = new Intent(ErrorActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                        finish();
                    }
                }
            });
        } /*else if(getTextError.equals("Error")){
            error_animationView.setAnimation("error.json");
            error_animationView.setVisibility(View.VISIBLE);
            String restart = "Restart";
            refresh_btn.setText(restart);
            textView_error.setText(getText);
            refresh_btn.setVisibility(View.VISIBLE);
            refresh_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline()) {
                        Intent intent = new Intent(ErrorActivity.this, Splash_Screen.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                        finishAffinity();
                    }
                }
            });

        }*/ else if (getText.equals("This App is not for users! Please log in using Agrigoww from playstore!")) {
            error_animationView.setAnimation("error.json");
            error_animationView.setVisibility(View.VISIBLE);
            String restart = "CLOSE APP";
            refresh_btn.setText(restart);
            textView_error.setText(getText);
            refresh_btn.setVisibility(View.VISIBLE);
            refresh_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                    System.exit(0);
                }
            });
        }  else if (getText.equals("This App is not for Experts! Please log in using Agrigroww Experts!")) {
            error_animationView.setAnimation("error.json");
            error_animationView.setVisibility(View.VISIBLE);
            String restart = "CLOSE APP";
            refresh_btn.setText(restart);
            textView_error.setText(getText);
            refresh_btn.setVisibility(View.VISIBLE);
            refresh_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                    System.exit(0);
                }
            });
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}