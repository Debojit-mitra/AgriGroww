package com.dmsskbm.agrigroww.expert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dmsskbm.agrigroww.expert.fragments.homeFragment;
import com.dmsskbm.agrigroww.expert.fragments.profileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;


public class MainActivity extends AppCompatActivity {


    FragmentTransaction fragmentTransaction;
    FragmentTransaction fragmentTransaction2;
    FrameLayout main_frame_layout;
    SmoothBottomBar bottomBar;
    LottieAnimationView login_animationView;
    String intentString, otherLanguagesAvailable;

    private Fragment mHomeFragment = new homeFragment();
    private Fragment mProfileFragment = new profileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomBar = findViewById(R.id.bottomBar);
        main_frame_layout = findViewById(R.id.main_frame_layout);

        intentString = getIntent().getStringExtra("frgToLoad");
        //fragment loads
        if(intentString == null){
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            Fragment homeFragment = mHomeFragment;
            Fragment profileFragment = mProfileFragment;
            fragmentTransaction2.replace(R.id.main_frame_layout, profileFragment);
            fragmentTransaction2.hide(profileFragment);
            fragmentTransaction2.commit();//loaded multiple fragments while app start to remove shutters
            fragmentTransaction.add(R.id.main_frame_layout, homeFragment);
            fragmentTransaction.commit();
        }else if(intentString.equals("mProfileFragment")){
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment Fragment = mProfileFragment;
            fragmentTransaction.replace(R.id.main_frame_layout, Fragment);
            fragmentTransaction.commit();
            bottomBar.setItemActiveIndex(2);
        }

        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            Fragment fragment = null;
            @Override
            public boolean onItemSelect(int i) {

                switch (i) {
                    case 0:
                        fragment = mHomeFragment;
                        break;

                    case 1:
                        fragment = mProfileFragment;
                        break;
                }
                if (fragment != null) {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
                    //fragmentTransaction.add(R.id.main_frame_layout, fragment);
                    if (fragment == mHomeFragment) {
                        fragmentTransaction.show(mHomeFragment);
                        fragmentTransaction.hide(mProfileFragment);
                    } else if (fragment == mProfileFragment) {
                        if (getSupportFragmentManager().getFragments().contains(mProfileFragment)) {
                            fragmentTransaction.show(mProfileFragment);
                            fragmentTransaction.hide(mHomeFragment);
                        } else {
                            fragmentTransaction.add(R.id.main_frame_layout, fragment);
                            fragmentTransaction.show(fragment);
                            fragmentTransaction.hide(mHomeFragment);
                        }
                    }
                    //fragmentTransaction.replace(R.id.main_frame_layout,fragment);
                    fragmentTransaction.commit();

                } else {
                    Log.e("Fragment", "Error in creating fragment");
                }


                return true;
            }

        });
        //exception if data of user doesnt exits
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        DatabaseReference checkNewUserDetails = FirebaseDatabase.getInstance().getReference().child("ExpertDetails");
        DatabaseReference check2 = checkNewUserDetails.child(authProfile.getCurrentUser().getUid());
        check2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.hasChildren()) {
                    AlertDialog.Builder register = new AlertDialog.Builder(MainActivity.this, R.style.RoundedCornersDialog);
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_alert_dialog, null);
                    Button ok_button = view.findViewById(R.id.ok_button);
                    ok_button.setVisibility(View.VISIBLE);
                    login_animationView = view.findViewById(R.id.custom_animationView);
                    login_animationView.setAnimation("error.json");
                    login_animationView.setVisibility(View.VISIBLE);

                    int width = 500;
                    int height = 500;
                    RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                    parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    login_animationView.setLayoutParams(parms);

                    TextView custom_textview = view.findViewById(R.id.custom_textview);
                    String custom_text = "No registered profile found!";
                    custom_textview.setText(custom_text);
                    custom_textview.setVisibility(View.VISIBLE);

                    register.setView(view);
                    AlertDialog alertDialog = register.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                            finish();
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       /* //checking update
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                String updaterLink = "https://api.npoint.io/4a2846553d54abf8ed84";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, updaterLink, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("volley",response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String latestVersion = jsonObject.getString("latestVersion");
                            String githubUrl = jsonObject.getString("url");
                            String apkDownloadUrl = jsonObject.getString("download_url");
                            String updateRelease = jsonObject.getString("update_release");
                            String releaseNotes = jsonObject.getString("releaseNotes");
                            String updateCancelable = jsonObject.getString("update_cancelable");
                            String otherLanguages = jsonObject.getString("other_languages");
                            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

                            SharedPreferences OtherLanguagesAvailable = getSharedPreferences("OtherLanguages", Context.MODE_PRIVATE);
                            SharedPreferences languageOptions = getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                            String savedLanguage = languageOptions.getString("SelectedLanguage", "English");
                            if(otherLanguages.equals("available")){
                                otherLanguagesAvailable = "YES";
                                OtherLanguagesAvailable.edit().putString("otherLanguagesAvailable", otherLanguagesAvailable).apply();
                            }else if(!savedLanguage.equals("English")){
                                otherLanguagesAvailable = "NO";
                                OtherLanguagesAvailable.edit().putString("otherLanguagesAvailable", otherLanguagesAvailable).apply();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        AlertDialog.Builder languageAlert = new AlertDialog.Builder(MainActivity.this, R.style.RoundedCornersDialog);
                                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_alert_dialog, null);
                                        TextView custom_textview;
                                        Button ok_button;
                                        ok_button = view.findViewById(R.id.ok_button);
                                        ok_button.setVisibility(View.VISIBLE);
                                        LottieAnimationView custom_animationView = view.findViewById(R.id.custom_animationView);
                                        custom_animationView.setAnimation("namaste.lottie");
                                        custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                                        int width = 400;
                                        int height = 400;
                                        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                                        parms.setMargins(10,50,10,10);
                                        parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                        custom_animationView.setLayoutParams(parms);
                                        custom_textview = view.findViewById(R.id.custom_textview);
                                        custom_textview.setText(R.string.language_not_available);
                                        custom_textview.setVisibility(View.VISIBLE);

                                        languageAlert.setView(view);
                                        AlertDialog alertDialog = languageAlert.create();
                                        alertDialog.setCancelable(false);

                                        ok_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                               alertDialog.dismiss();
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                }, 500);
                            }



                            if(!versionName.equals(latestVersion) && BuildConfig.BUILD_TYPE == "release"){

                                AlertDialog.Builder updater = new AlertDialog.Builder(MainActivity.this, R.style.RoundedCornersDialog);
                                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.updater_dialog, null);
                                TextView textview_update_version, description_release_notes, description_github_url, textview_update_date;
                                Button ok_button, cancel_button;
                                textview_update_version = view.findViewById(R.id.textview_update_version);
                                textview_update_date = view.findViewById(R.id.textview_update_date);
                                description_release_notes = view.findViewById(R.id.description_release_notes);
                                description_github_url = view.findViewById(R.id.description_github_url);
                                ok_button = view.findViewById(R.id.ok_button);
                                cancel_button = view.findViewById(R.id.cancel_button);

                                textview_update_version.setText("Version: "+latestVersion);
                                description_release_notes.setText(releaseNotes);
                                description_github_url.setText("Repo Url: "+githubUrl);
                                textview_update_date.setText("Date: "+updateRelease);

                                updater.setView(view);
                                AlertDialog alertDialog = updater.create();
                                alertDialog.setCancelable(false);

                                if(updateCancelable.equals("YES")){

                                    cancel_button.setVisibility(View.VISIBLE);
                                    cancel_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                                ok_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkDownloadUrl));
                                        Intent chooseIntent = Intent.createChooser(intent, "Choose from below");
                                        startActivity(chooseIntent);
                                    }
                                });
                                alertDialog.show();
                            }

                        }catch (Exception e){
                                e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);


            }
        }, 500);
*/
        //for chatActivity
        SharedPreferences options = MainActivity.this.getSharedPreferences("checkButtonPressed", Context.MODE_PRIVATE);
        String checkButton = options.getString("NewChatButtonPressed", "NO");
        if(checkButton.equals("YES")){
            options.edit().putString("NewChatButtonPressed", "NO").apply();
        }


    }

    //will not exit or close the app when back is pressed rather it will go to background
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}