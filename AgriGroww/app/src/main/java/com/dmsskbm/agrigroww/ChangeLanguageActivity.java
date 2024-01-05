package com.dmsskbm.agrigroww;

import static com.dmsskbm.agrigroww.fragments.profileFragment.languages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {

    Spinner selectLanguageFromSpinner;
    Button button_select_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        selectLanguageFromSpinner = findViewById(R.id.selectLanguageFromSpinner);
        button_select_language = findViewById(R.id.button_select_language);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChangeLanguageActivity.this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectLanguageFromSpinner.setAdapter(adapter);

        SharedPreferences options = ChangeLanguageActivity.this.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
        String savedLanguage = options.getString("SelectedLanguage", "English");
        if(savedLanguage.equals("English")){
            selectLanguageFromSpinner.setSelection(0);
        }else if(savedLanguage.equals("অসমীয়া")){
            selectLanguageFromSpinner.setSelection(1);
        } else if (savedLanguage.equals("हिंदी")) {
            selectLanguageFromSpinner.setSelection(2);
        }

        selectLanguageFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLang = adapterView.getItemAtPosition(i).toString();
                if(selectedLang.equals("English")){

                    button_select_language.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences options = ChangeLanguageActivity.this.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                            options.edit().putString("SelectedLanguage", "English").apply();
                            setLocal("en");
                            Intent refresh= new Intent(ChangeLanguageActivity.this, Splash_Screen.class);
                            refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(refresh);
                        }
                    });

                } else if (selectedLang.equals("অসমীয়া")) {
                    button_select_language.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences options = ChangeLanguageActivity.this.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                            options.edit().putString("SelectedLanguage", "অসমীয়া").apply();
                            setLocal("as");
                            Intent refresh= new Intent(ChangeLanguageActivity.this, Splash_Screen.class);
                            refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(refresh);
                        }
                    });

                } else if(selectedLang.equals("हिंदी")){
                    button_select_language.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences options = ChangeLanguageActivity.this.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                            options.edit().putString("SelectedLanguage", "हिंदी").apply();
                            setLocal("hi");
                            Intent refresh= new Intent(ChangeLanguageActivity.this, Splash_Screen.class);
                            refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(refresh);
                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void setLocal(String languageCode){
        Resources resources = ChangeLanguageActivity.this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}