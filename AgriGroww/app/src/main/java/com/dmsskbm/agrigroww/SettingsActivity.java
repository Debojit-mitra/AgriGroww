package com.dmsskbm.agrigroww;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat description_server_switch, switch_scroll_animation, follow_system_theme_switch, theme_switch;
    ImageButton description_server_info_btn, description_scroll_animation_btn, back_button;
    LottieAnimationView custom_animationView;
    CardView card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        card4 = findViewById(R.id.card4);

        description_server_switch = findViewById(R.id.description_server_switch);
        switch_scroll_animation = findViewById(R.id.switch_scroll_animation);
        follow_system_theme_switch = findViewById(R.id.follow_system_theme_switch);
        theme_switch = findViewById(R.id.theme_switch);

        description_server_info_btn = findViewById(R.id.description_server_info_btn);
        description_scroll_animation_btn = findViewById(R.id.description_scroll_animation_btn);
        back_button = findViewById(R.id.back_button);

        SharedPreferences options = getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
        String savedValue = options.getString("Server", "NotSelected");
        if(savedValue.equals("Selected")){
            description_server_switch.setChecked(true);
        }else {
            description_server_switch.setChecked(false);
        }
        description_server_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    SharedPreferences options = getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                    options.edit().putString("Server", "Selected").apply();
                    Toast.makeText(SettingsActivity.this, "Server 2 Selected!", Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences options = getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                    options.edit().putString("Server", "NotSelected").apply();
                    Toast.makeText(SettingsActivity.this, "Server 1 Selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        description_server_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder info = new AlertDialog.Builder(SettingsActivity.this, R.style.RoundedCornersDialog);
                View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.custom_alert_dialog, null);
                Button cancel_button = view.findViewById(R.id.cancel_button);
                cancel_button.setVisibility(View.VISIBLE);
                cancel_button.setText(R.string.ok);
                custom_animationView = view.findViewById(R.id.custom_animationView);
                custom_animationView.setAnimation("info.lottie");
                custom_animationView.setVisibility(View.VISIBLE);
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                int width = 400;
                int height = 400;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);
                TextView custom_textview = view.findViewById(R.id.custom_textview);
                String custom_text = getString(R.string.description_server_settings_desc);
                custom_textview.setText(custom_text);
                custom_textview.setVisibility(View.VISIBLE);
                info.setView(view);
                AlertDialog alertDialog = info.create();
                alertDialog.setCanceledOnTouchOutside(false);
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        String savedValueAnimation = options.getString("ScrollAnimation", "NotSelected");
        if(savedValueAnimation.equals("Selected")){
            switch_scroll_animation.setChecked(true);
        }else {
            switch_scroll_animation.setChecked(false);
        }
        switch_scroll_animation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    options.edit().putString("ScrollAnimation", "Selected").apply();
                    Toast.makeText(SettingsActivity.this, "Scroll animation turned on! Restart app then identify the picture again to see difference.", Toast.LENGTH_LONG).show();
                }else {
                    options.edit().putString("ScrollAnimation", "NotSelected").apply();
                    Toast.makeText(SettingsActivity.this, "Scroll animation turned off!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        description_scroll_animation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder info = new AlertDialog.Builder(SettingsActivity.this, R.style.RoundedCornersDialog);
                View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.custom_alert_dialog, null);
                Button cancel_button = view.findViewById(R.id.cancel_button);
                cancel_button.setVisibility(View.VISIBLE);
                cancel_button.setText(R.string.ok);
                custom_animationView = view.findViewById(R.id.custom_animationView);
                custom_animationView.setAnimation("info.lottie");
                custom_animationView.setVisibility(View.VISIBLE);
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                int width = 400;
                int height = 400;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);
                TextView custom_textview = view.findViewById(R.id.custom_textview);
                String custom_text = getString(R.string.scroll_animation_settings_desc);
                custom_textview.setText(custom_text);
                custom_textview.setVisibility(View.VISIBLE);
                info.setView(view);
                AlertDialog alertDialog = info.create();
                alertDialog.setCanceledOnTouchOutside(false);
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //SharedPreferences options = getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
        //options.edit().putString("follow_system_theme_mode", "follow").apply();
        String savedValueFollow_system_theme_mode = options.getString("follow_system_theme_mode", "follow");
        if(savedValueFollow_system_theme_mode.equals("follow")){
            follow_system_theme_switch.setChecked(true);
            card4.setVisibility(View.GONE);
        }else{
            follow_system_theme_switch.setChecked(false);
            card4.setVisibility(View.VISIBLE);
        }

        follow_system_theme_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked()) {
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_FOLLOW_SYSTEM);
                    options.edit().putString("follow_system_theme_mode", "follow").apply();
                    options.edit().remove("dark_mode").apply();
                    card4.setVisibility(View.GONE);
                }else {
                    card4.setVisibility(View.VISIBLE);
                    switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            theme_switch.setChecked(true);
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            theme_switch.setChecked(false);
                            break;
                    }
                    options.edit().putString("follow_system_theme_mode", "unfollow").apply();
                }
            }
        });


        if (savedValueFollow_system_theme_mode.equals("unfollow")){

            String savedValueDark_mode = "";
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    options.edit().putString("dark_mode", "ON").apply();
                    savedValueDark_mode = options.getString("dark_mode", "ON");
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    options.edit().putString("dark_mode", "OFF").apply();
                    savedValueDark_mode = options.getString("dark_mode", "OFF");
                    break;
            }

            if(savedValueDark_mode.equals("ON")){
                theme_switch.setChecked(true);
            }else{
                theme_switch.setChecked(false);
            }
        }
        theme_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);
                    options.edit().putString("dark_mode", "ON").apply();
                }else{
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);
                    options.edit().putString("dark_mode", "OFF").apply();
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}