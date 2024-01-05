package com.dmsskbm.agrigroww;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SplashScreen splashScreen = SplashScreen.installSplashScreen(Splash_Screen.this);
            splashScreen.setKeepOnScreenCondition(() -> false );
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                        splashScreenView,
                        View.TRANSLATION_Y,
                        0f,
                        -splashScreenView.getHeight()
                );
                slideUp.setInterpolator(new AnticipateInterpolator());
                slideUp.setDuration(1000L);
                Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                finish();

                // Call SplashScreenView.remove at the end of your custom animation.
                slideUp.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreenView.remove();
                    }
                });

                // Run your animation.
                slideUp.start();
            });
        }else{
            setTheme(R.style.Theme_AgriGroww_Splash_Screen);
        }*/

        //dark_mode_theme
        SharedPreferences options = getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
        String savedValueTheme_mode = options.getString("dark_mode", "null");
        if (!savedValueTheme_mode.equals("null")) {
            if (savedValueTheme_mode.equals("ON")) {
                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_YES);
            } else {
                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_NO);
            }

        }

        FirebaseApp.initializeApp(/*context=*/ Splash_Screen.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());


        SharedPreferences languageOptions = getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
        String savedLanguage = languageOptions.getString("SelectedLanguage", "English");
        if (savedLanguage.equals("English")) {
            setLocal("en");
        } else if (savedLanguage.equals("অসমীয়া")) {
            setLocal("as");
        } else if (savedLanguage.equals("हिंदी")) {
            setLocal("hi");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        //for firebase cache
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.e("PersistenceErrorSplashScreen", String.valueOf(e));
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseAuth authProfile = FirebaseAuth.getInstance();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                try {
                    //checking internet
                    if (isOnline()) {

                        //checking if user exits in database
                        if (authProfile.getCurrentUser() != null) {
                            DatabaseReference checkNewUserDetails = FirebaseDatabase.getInstance().getReference().child("userDetails");
                            DatabaseReference check2 = checkNewUserDetails.child(authProfile.getCurrentUser().getUid());
                            check2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    //checking if user is a new user or already registered
                                    if (Objects.equals(snapshot.getValue(), "newuser")) {
                                        Intent intent = new Intent(Splash_Screen.this, RegistrationActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                                        finish();
                                    } else {
                                        //if any database error occurs and no user was found then it will auto log out
                                        if (!snapshot.hasChildren()) {
                                            authProfile.signOut();
                                            Intent intent = new Intent(Splash_Screen.this, ErrorActivity.class);
                                            intent.putExtra("ExtraText", "You have been signed out!");
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                                            finish();
                                        } else {
                                            checkNewUserDetails.child(authProfile.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.hasChild("isBanned")) {
                                                        authProfile.signOut();
                                                        Intent intent = new Intent(Splash_Screen.this, ErrorActivity.class);
                                                        intent.putExtra("ExtraText", "userBanned");
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                                                        finish();
                                                    } else {
                                                        Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e("DatabaseErrorSplash", error.getMessage());
                                                    Toast.makeText(Splash_Screen.this, "Database Error!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                /*Intent intent = new Intent(Splash_Screen.this, ErrorActivity.class);
                                intent.putExtra("Error","Error");
                                intent.putExtra("ExtraText","Database Error Occurred!");
                                startActivity(intent);
                                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                                finish();*/
                                }
                            });

                        } else {
                            Intent intent = new Intent(Splash_Screen.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.bottom_up, R.anim.fadeout);
                            finish();
                        }

                    } else {
                        Intent intent = new Intent(Splash_Screen.this, ErrorActivity.class);
                        intent.putExtra("ExtraText", "No Internet Connection!");
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Log.e("SplashScreenError", e.getMessage());
                }
            }
        }, 800);

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void setLocal(String languageCode) {
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}