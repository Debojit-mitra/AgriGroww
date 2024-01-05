package com.dmsskbm.agrigroww.expert.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dmsskbm.agrigroww.expert.EditProfileActivity;
import com.dmsskbm.agrigroww.expert.ErrorActivity;
import com.dmsskbm.agrigroww.expert.LoginActivity;
import com.dmsskbm.agrigroww.expert.R;
import com.dmsskbm.agrigroww.expert.ReadWriteUserDetails;
import com.dmsskbm.agrigroww.expert.SettingsActivity;
import com.dmsskbm.agrigroww.expert.Splash_Screen;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class profileFragment extends Fragment {

    FirebaseAuth authProfile;
    ImageView profile_image;
    ImageButton profile_userId_copy_btn, description_server_info_btn;
    TextView profile_name, profile_userId, profile_dateJoin, profile_phNumber;
    RelativeLayout relative_edit_profile, relative_give_feedback, relative_change_language, relative_settings, relative_logout;
    LottieAnimationView custom_animationView;
    private String fullName, profileImage, dateJoined, userId, phone;
    ProgressBar progressBar;
    KenBurnsView animatedImageBackground;
    public static final String[] languages = {"English","অসমীয়া","हिंदी"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        authProfile = FirebaseAuth.getInstance();
        //get instance of the current user
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        profile_image = view.findViewById(R.id.profile_image);
        profile_name = view.findViewById(R.id.profile_name);
        profile_userId = view.findViewById(R.id.profile_userId);
        profile_dateJoin = view.findViewById(R.id.profile_dateJoin);
        profile_phNumber = view.findViewById(R.id.profile_phNumber);
        progressBar = view.findViewById(R.id.progressBar);
        profile_userId_copy_btn = view.findViewById(R.id.profile_userId_copy_btn);
        relative_logout = view.findViewById(R.id.relative_logout);
        relative_edit_profile = view.findViewById(R.id.relative_edit_profile);
        relative_change_language = view.findViewById(R.id.relative_change_language);
        relative_give_feedback = view.findViewById(R.id.relative_give_feedback);
        relative_settings = view.findViewById(R.id.relative_settings);
        animatedImageBackground = view.findViewById(R.id.animatedImageBackground);

        if(firebaseUser != null){
            showUserProfile(view);
        }else {
            authProfile.signOut();
            Intent intent = new Intent(getActivity(), ErrorActivity.class);
            intent.putExtra("ExtraText","You have been signed out!");
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
            requireActivity().finish();
        }

       /* profile_userId_copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clipData = android.content.ClipData.newPlainText("Unique Id", userId);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(), "Unique Id copied!", Toast.LENGTH_SHORT).show();
            }
        });*/

        relative_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        relative_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //dialog box for logout
                AlertDialog.Builder logout = new AlertDialog.Builder(requireContext(), R.style.RoundedCornersDialog);
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null);
                Button ok_button = view.findViewById(R.id.ok_button);
                Button cancel_button = view.findViewById(R.id.cancel_button);
                cancel_button.setVisibility(View.VISIBLE);
                ok_button.setVisibility(View.VISIBLE);
                custom_animationView = view.findViewById(R.id.custom_animationView);
                custom_animationView.setAnimation("loggedout.json");
                custom_animationView.setVisibility(View.VISIBLE);
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);

                int width = 800;
                int height = 600;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);

                TextView custom_textview = view.findViewById(R.id.custom_textview);
                String custom_text = getString(R.string.logging_out);
                custom_textview.setText(custom_text);
                custom_textview.setVisibility(View.VISIBLE);

                logout.setView(view);
                AlertDialog alertDialog = logout.create();
                alertDialog.setCanceledOnTouchOutside(false);
                ok_button.setTextColor(getActivity().getResources().getColor(R.color.red));
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOnline()) {
                        alertDialog.dismiss();
                        try {
                            authProfile.signOut();
                            if (authProfile.getCurrentUser() == null) {
                                Toast.makeText(getActivity(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                requireActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                                requireActivity().finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        }else {
                            Intent intent = new Intent(getActivity(), ErrorActivity.class);
                            intent.putExtra("ExtraText","No Internet Connection!");
                            startActivity(intent);
                        }
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
        });



        relative_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        relative_give_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Intent intent = new Intent(getActivity(), FeedbackActivity.class);
             //   startActivity(intent);

            }
        });

        /*relative_change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewB) {

                AlertDialog.Builder logout = new AlertDialog.Builder(requireContext(), R.style.RoundedCornersDialog);
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null);
                Button ok_button = view.findViewById(R.id.ok_button);
                Button cancel_button = view.findViewById(R.id.cancel_button);
                cancel_button.setVisibility(View.VISIBLE);
                ok_button.setVisibility(View.VISIBLE);
                custom_animationView = view.findViewById(R.id.custom_animationView);
                custom_animationView.setAnimation("change_language.json");
                custom_animationView.setVisibility(View.VISIBLE);
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);

                int width = 900;
                int height = 800;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);

                Spinner selectFromSpinner = view.findViewById(R.id.selectFromSpinner);
                ImageView selectFromSpinner_dropdown = view.findViewById(R.id.selectFromSpinner_dropdown);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, languages);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectFromSpinner.setAdapter(adapter);
                SharedPreferences options = requireActivity().getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                String savedLanguage = options.getString("SelectedLanguage", "English");
                if(savedLanguage.equals("English")){
                    selectFromSpinner.setSelection(0);
                }else if(savedLanguage.equals("অসমীয়া")){
                    selectFromSpinner.setSelection(1);
                } else if (savedLanguage.equals("हिंदी")) {
                    selectFromSpinner.setSelection(2);
                }
                selectFromSpinner.setVisibility(View.VISIBLE);
                selectFromSpinner_dropdown.setVisibility(View.VISIBLE);

                logout.setView(view);
                AlertDialog alertDialog = logout.create();
                alertDialog.setCanceledOnTouchOutside(false);

                selectFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedLang = adapterView.getItemAtPosition(i).toString();
                        if(selectedLang.equals("English") && !savedLanguage.equals("English")){

                            ok_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences options = requireActivity().getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                                    options.edit().putString("SelectedLanguage", "English").apply();
                                    alertDialog.dismiss();
                                    setLocal("en");
                                    Intent refresh= new Intent(getActivity(), Splash_Screen.class);
                                    refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(refresh);
                                }
                            });

                        } else if (selectedLang.equals("অসমীয়া") && !savedLanguage.equals("অসমীয়া")) {
                            ok_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences options = requireActivity().getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                                    options.edit().putString("SelectedLanguage", "অসমীয়া").apply();
                                    alertDialog.dismiss();
                                    setLocal("as");
                                    Intent refresh= new Intent(getActivity(), Splash_Screen.class);
                                    refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(refresh);
                                }
                            });

                        } else if(selectedLang.equals("हिंदी") && !savedLanguage.equals("हिंदी")){
                            ok_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences options = requireActivity().getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
                                    options.edit().putString("SelectedLanguage", "हिंदी").apply();
                                    alertDialog.dismiss();
                                    setLocal("hi");
                                    Intent refresh= new Intent(getActivity(), Splash_Screen.class);
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
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });*/







        return view;
    }

   /* public void setLocal(String languageCode){
        Resources resources = requireContext().getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }*/

    private void showUserProfile(View view) {

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ExpertDetails");
        referenceProfile.child(authProfile.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readWriteUserDetails!=null){

                    fullName = readWriteUserDetails.fullName;
                    profileImage = readWriteUserDetails.profileImage;
                    dateJoined = readWriteUserDetails.dateJoined;
                    userId = readWriteUserDetails.userId;
                    phone = readWriteUserDetails.phone;
                    //useful for saving message name
                    try{
                        SharedPreferences options = requireActivity().getSharedPreferences("ExpertDetails", Context.MODE_PRIVATE);
                        options.edit().putString("name", readWriteUserDetails.fullName).apply();
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(profileImage.equals("n/a")){

                        profile_image.setImageResource(R.drawable.user);
                       // Glide.with(view.getContext()).load(R.drawable.user).centerCrop().into(profile_image);
                        progressBar.setVisibility(View.GONE);
                    }else{
                        Glide.with(view.getContext()).load(profileImage).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .placeholder(R.drawable.user)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(profile_image);

                        Glide.with(animatedImageBackground.getContext()).load(profileImage)
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(animatedImageBackground);
                    }
                    String helloName = "Hello, Expert!\n"+fullName;
                    profile_name.setText(helloName);
                    profile_dateJoin.setText(dateJoined);
                    profile_userId.setText(userId);
                    profile_phNumber.setText(phone);
                    referenceProfile.keepSynced(true);



                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}