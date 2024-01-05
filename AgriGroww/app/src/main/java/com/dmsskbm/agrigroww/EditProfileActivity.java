package com.dmsskbm.agrigroww;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    ImageView update_profile_image, imageView_verified_email;
    ImageButton back_button;
    EditText edittext_name_editprofile, edittext_dateofbirth_editprofile, edittext_email_editprofile;
    EditText inputotp1, inputotp2, inputotp3, inputotp4, inputotp5, inputotp6;
    LottieAnimationView custom_animationView;
    RadioGroup radio_group_editprofile_gender;
    RadioButton radioButtonRegisterGenderSelected;
    Button button_verify_otp;
    TextView change_email, remove_email, remove_profile_picture;
    ProgressBar progressBar;
    Button button_update_profile, button_delete_profile;
    private String fullName, profileImage, dob, gender, dateJ, email;
    Uri uriImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatePickerDialog picker;

    String TagImage = "oldImage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (isOnline()) {

            authProfile = FirebaseAuth.getInstance();
            //get instance of the current user
            firebaseUser = authProfile.getCurrentUser();

            update_profile_image = findViewById(R.id.update_profile_image);
            edittext_name_editprofile = findViewById(R.id.edittext_name_editprofile);
            edittext_email_editprofile = findViewById(R.id.edittext_email_editprofile);
            edittext_dateofbirth_editprofile = findViewById(R.id.edittext_dateofbirth_editprofile);
            imageView_verified_email = findViewById(R.id.imageView_verified_email);
            progressBar = findViewById(R.id.progressBar);
            change_email = findViewById(R.id.change_email);
            remove_email = findViewById(R.id.remove_email);
            remove_profile_picture = findViewById(R.id.remove_profile_picture);
            button_update_profile = findViewById(R.id.button_update_profile);
            radio_group_editprofile_gender = findViewById(R.id.radio_group_editprofile_gender);
            radio_group_editprofile_gender.clearCheck();
            button_delete_profile = findViewById(R.id.button_delete_profile);
            back_button = findViewById(R.id.back_button);
            showProfile();

            update_profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageChoser();
                }
            });

            edittext_dateofbirth_editprofile.setFocusable(false);
            edittext_dateofbirth_editprofile.setCursorVisible(false);
            edittext_dateofbirth_editprofile.setClickable(true);
            edittext_dateofbirth_editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    //date picker dialog
                    picker = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                            String dofB = dayOfMonth + "/" + (month + 1) + "/" + year;
                            edittext_dateofbirth_editprofile.setText(dofB);

                        }
                    }, year, month, day);
                    final Calendar cmax = Calendar.getInstance();
                    cmax.set(2003, 01, 01);
                    picker.getDatePicker().setMaxDate(cmax.getTimeInMillis());
                    picker.show();

                }
            });

            remove_profile_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder removeEmail = new AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
                    View view1 = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog, null);
                    Button ok_button, cancel_button;
                    TextView custom_textview;
                    LottieAnimationView custom_animationView;
                    ok_button = view1.findViewById(R.id.ok_button);
                    cancel_button = view1.findViewById(R.id.cancel_button);
                    ok_button.setText(R.string.confirm);
                    ok_button.setTextColor(getResources().getColor(R.color.red));
                    cancel_button.setText(R.string.cancel);
                    custom_animationView = view1.findViewById(R.id.custom_animationView);
                    custom_textview = view1.findViewById(R.id.custom_textview);
                    custom_animationView.setAnimation("red_warning.json");
                    custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                    String custom_text = "Please confirm, you want to remove your profile pic?";
                    custom_textview.setText(custom_text);
                    ok_button.setVisibility(View.VISIBLE);
                    cancel_button.setVisibility(View.VISIBLE);
                    custom_textview.setVisibility(View.VISIBLE);
                    int width = 400;
                    int height = 400;
                    RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                    parms.setMargins(10,10,10,10);
                    parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    custom_animationView.setLayoutParams(parms);
                    removeEmail.setView(view1);
                    AlertDialog alertDialog = removeEmail.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
                            referenceProfile.child(authProfile.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        referenceProfile.child(authProfile.getCurrentUser().getUid()).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.getValue().equals("n/a")){
                                                    Toast.makeText(EditProfileActivity.this,"Please set a profile picture to remove it!", Toast.LENGTH_SHORT).show();
                                                    alertDialog.dismiss();
                                                }else {
                                                    referenceProfile.child(authProfile.getCurrentUser().getUid()).child("profileImage").setValue("n/a").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(EditProfileActivity.this,"Profile picture has been removed!", Toast.LENGTH_SHORT).show();
                                                            alertDialog.dismiss();
                                                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                                            intent.putExtra("frgToLoad", "mProfileFragment");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(EditProfileActivity.this,"Failed to remove profile picture!", Toast.LENGTH_SHORT).show();
                                                            alertDialog.dismiss();
                                                        }
                                                    });
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
                                    Toast.makeText(EditProfileActivity.this,"Failed to remove email!", Toast.LENGTH_SHORT).show();
                                    Log.e("Database Error", error.getMessage());
                                }
                            });
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();

                }
            });

            button_delete_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder deleteOtpVerify = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
                    View view = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog, null);
                    Button ok_button = view.findViewById(R.id.ok_button);
                    Button cancel_button = view.findViewById(R.id.cancel_button);
                    cancel_button.setVisibility(View.VISIBLE);
                    ok_button.setVisibility(View.VISIBLE);
                    custom_animationView = view.findViewById(R.id.custom_animationView);
                    custom_animationView.setAnimation("red_warning.json");
                    custom_animationView.setVisibility(View.VISIBLE);
                    custom_animationView.setRepeatCount(LottieDrawable.INFINITE);

                    int width = 400;
                    int height = 400;
                    RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                    parms.setMargins(10, 10, 10, 10);
                    parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    custom_animationView.setLayoutParams(parms);

                    TextView custom_textview = view.findViewById(R.id.custom_textview);
                    String custom_text = getString(R.string.profile_deleting);
                    custom_textview.setText(custom_text);
                    custom_textview.setVisibility(View.VISIBLE);

                    deleteOtpVerify.setView(view);
                    AlertDialog alertDialog = deleteOtpVerify.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    ok_button.setTextColor(EditProfileActivity.this.getResources().getColor(R.color.red));
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            try {

                                //authProfile.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

                                String phoneNumber = authProfile.getCurrentUser().getPhoneNumber();
                                if (phoneNumber != null) {

                                    progressBar.setVisibility(View.VISIBLE);
                                    button_delete_profile.setVisibility(View.GONE);
                                    button_update_profile.setVisibility(View.GONE);

                                    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

                                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            progressBar.setVisibility(View.GONE);
                                            button_delete_profile.setVisibility(View.VISIBLE);
                                            button_update_profile.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            progressBar.setVisibility(View.GONE);
                                            button_delete_profile.setVisibility(View.VISIBLE);
                                            button_update_profile.setVisibility(View.VISIBLE);
                                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String otpsent, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            progressBar.setVisibility(View.GONE);
                                            button_delete_profile.setVisibility(View.INVISIBLE);
                                            button_update_profile.setVisibility(View.VISIBLE);
                                            otpVerifyAlertDialog(otpsent);

                                        }
                                    };
                                    PhoneAuthOptions options =
                                            PhoneAuthOptions.newBuilder(authProfile)
                                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                    .setActivity(EditProfileActivity.this)                 // Activity (for callback binding)
                                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);

                                }


                            } catch (Exception e) {
                                button_verify_otp.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            button_delete_profile.setVisibility(View.VISIBLE);
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            });

            button_update_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name, dateob, gen, ema;
                    name = edittext_name_editprofile.getText().toString().trim();
                    dateob = edittext_dateofbirth_editprofile.getText().toString().trim();
                    int selectedGenderId = radio_group_editprofile_gender.getCheckedRadioButtonId();
                    radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
                    gen = radioButtonRegisterGenderSelected.getText().toString().trim();

                    // String oldUri = update_profile_image.getTag().toString();
                    // Log.e("olduri",oldUri);

                    if (!fullName.equals(name) || !dob.equals(dateob) || !gender.equals(gen) || !TagImage.equals("oldImage")) {
                        if (!name.equals("")) {
                            edittext_name_editprofile.clearFocus();

                            progressBar.setVisibility(View.VISIBLE);
                            button_delete_profile.setVisibility(View.GONE);
                            button_update_profile.setVisibility(View.GONE);

                            if (TagImage.equals("newImage") && (uriImage != null)) {

                                String textFullName, textDoB, textGender, textDateJoined;
                                textFullName = name;
                                textDoB = dateob;
                                textGender = gen;
                                textDateJoined = dateJ;

                                firebaseStorage = FirebaseStorage.getInstance();

                                StorageReference storageReference = firebaseStorage.getReference("ProfilePics");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                                String currentDateandTime = sdf.format(new Date());
                                Log.e("uid", authProfile.getCurrentUser().getUid());
                                StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid()).child(String.valueOf(currentDateandTime) + ".jpg");
                                fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                Uri downloadUri = uri;
                                                String textProfileImage = downloadUri.toString();
                                                String textPhone = authProfile.getCurrentUser().getPhoneNumber();
                                                String textUserId = authProfile.getCurrentUser().getUid();
                                                String textEmail = "n/a";
                                                ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDoB, textGender, textProfileImage, textPhone, textDateJoined, textUserId, textEmail);
                                                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
                                                referenceProfile.child(authProfile.getCurrentUser().getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            progressBar.setVisibility(View.GONE);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                            final AlertDialog.Builder account_created = new AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
                                                            View view = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog, null);
                                                            custom_animationView = view.findViewById(R.id.custom_animationView);
                                                            custom_animationView.setAnimation("update_successful.lottie");
                                                            custom_animationView.setVisibility(View.VISIBLE);
                                                            custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                                                            account_created.setView(view);
                                                            AlertDialog alertDialog = account_created.create();
                                                            alertDialog.setCanceledOnTouchOutside(false);
                                                            alertDialog.show();
                                                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                                            progressBar.setVisibility(View.GONE);
                                                            button_delete_profile.setVisibility(View.VISIBLE);
                                                            button_update_profile.setVisibility(View.VISIBLE);

                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                                                    intent.putExtra("frgToLoad", "mProfileFragment");
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);
                                                                    alertDialog.dismiss();
                                                                }
                                                            }, 3000);

                                                        } else {
                                                            Toast.makeText(EditProfileActivity.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });


                            } else {

                                String textFullName, textDoB, textGender, textDateJoined;
                                textFullName = name;
                                textDoB = dateob;
                                textGender = gen;
                                textDateJoined = dateJ;

                                String textProfileImage = profileImage;
                                String textPhone = authProfile.getCurrentUser().getPhoneNumber();
                                String textUserId = authProfile.getCurrentUser().getUid();
                                String textEmail = "n/a";
                                ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDoB, textGender, textProfileImage, textPhone, textDateJoined, textUserId, textEmail);
                                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
                                referenceProfile.child(authProfile.getCurrentUser().getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            progressBar.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            final AlertDialog.Builder account_created = new AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
                                            View view = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog, null);
                                            custom_animationView = view.findViewById(R.id.custom_animationView);
                                            custom_animationView.setAnimation("update_successful.lottie");
                                            custom_animationView.setVisibility(View.VISIBLE);
                                            custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                                            account_created.setView(view);
                                            AlertDialog alertDialog = account_created.create();
                                            alertDialog.setCanceledOnTouchOutside(false);
                                            alertDialog.show();
                                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                            progressBar.setVisibility(View.GONE);
                                            button_delete_profile.setVisibility(View.VISIBLE);
                                            button_update_profile.setVisibility(View.VISIBLE);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                                    intent.putExtra("frgToLoad", "mProfileFragment");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    alertDialog.dismiss();
                                                }
                                            }, 3000);

                                        } else {
                                            Toast.makeText(EditProfileActivity.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Enter name to save details!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Edit details before updating!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Intent intent = new Intent(EditProfileActivity.this, ErrorActivity.class);
            intent.putExtra("ExtraText", "No Internet Connection!");
            startActivity(intent);
            finish();
        }
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, EmailVerificationActivity.class);
                intent.putExtra("extra","fromEditProfile");
                startActivity(intent);
            }
        });
        remove_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder removeEmail = new AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
                View view1 = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog, null);
                Button ok_button, cancel_button;
                TextView custom_textview;
                LottieAnimationView custom_animationView;
                ok_button = view1.findViewById(R.id.ok_button);
                cancel_button = view1.findViewById(R.id.cancel_button);
                ok_button.setText(R.string.confirm);
                ok_button.setTextColor(getResources().getColor(R.color.red));
                cancel_button.setText(R.string.cancel);
                custom_animationView = view1.findViewById(R.id.custom_animationView);
                custom_textview = view1.findViewById(R.id.custom_textview);
                custom_animationView.setAnimation("red_warning.json");
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                String custom_text = "Are you sure you want to remove your email?";
                custom_textview.setText(custom_text);
                ok_button.setVisibility(View.VISIBLE);
                cancel_button.setVisibility(View.VISIBLE);
                custom_textview.setVisibility(View.VISIBLE);
                int width = 400;
                int height = 400;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.setMargins(10,10,10,10);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);
                removeEmail.setView(view1);
                AlertDialog alertDialog = removeEmail.create();
                alertDialog.setCanceledOnTouchOutside(false);
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
                        referenceProfile.child(authProfile.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    referenceProfile.child(authProfile.getCurrentUser().getUid()).child("email").setValue("n/a");
                                    Toast.makeText(EditProfileActivity.this,"Email has been removed!", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    showProfile();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(EditProfileActivity.this,"Failed to remove email!", Toast.LENGTH_SHORT).show();
                                Log.e("Database Error", error.getMessage());
                            }
                        });
                    }
                });
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void otpVerifyAlertDialog(String otpsent) {

        AlertDialog.Builder deleteOtpVerify = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
        View view = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog_otpverify, null);
        button_verify_otp = view.findViewById(R.id.button_verify_otp);
        ImageButton cancel_button = view.findViewById(R.id.cancel_image_button);
        cancel_button.setVisibility(View.VISIBLE);
        button_verify_otp.setVisibility(View.VISIBLE);
        custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
        progressBar = view.findViewById(R.id.progressBar);

        int width = 800;
        int height = 800;
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
        parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
        custom_animationView.setLayoutParams(parms);

        deleteOtpVerify.setView(view);
        AlertDialog alertDialog = deleteOtpVerify.create();
        alertDialog.setCanceledOnTouchOutside(false);

        inputotp1 = view.findViewById(R.id.inputotp1);
        inputotp2 = view.findViewById(R.id.inputotp2);
        inputotp3 = view.findViewById(R.id.inputotp3);
        inputotp4 = view.findViewById(R.id.inputotp4);
        inputotp5 = view.findViewById(R.id.inputotp5);
        inputotp6 = view.findViewById(R.id.inputotp6);

        button_verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!inputotp1.getText().toString().trim().isEmpty() && !inputotp2.getText().toString().trim().isEmpty()
                        && !inputotp3.getText().toString().trim().isEmpty() && !inputotp4.getText().toString().trim().isEmpty()
                        && !inputotp4.getText().toString().trim().isEmpty() && !inputotp5.getText().toString().trim().isEmpty()
                        && !inputotp6.getText().toString().trim().isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);
                    button_verify_otp.setVisibility(View.GONE);

                    String checkingOtp = inputotp1.getText().toString() +
                            inputotp2.getText().toString() +
                            inputotp3.getText().toString() +
                            inputotp4.getText().toString() +
                            inputotp5.getText().toString() +
                            inputotp6.getText().toString();

                    if (otpsent != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otpsent, checkingOtp);
                        authProfile.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        String uidD = authProfile.getCurrentUser().getUid();

                                        authProfile.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseAuth authProfile = FirebaseAuth.getInstance();
                                                    DatabaseReference referenceDelete = FirebaseDatabase.getInstance().getReference("userDetails").child(uidD);
                                                    referenceDelete.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressBar.setVisibility(View.GONE);
                                                            authProfile.signOut();
                                                            alertDialog.dismiss();
                                                            Toast.makeText(EditProfileActivity.this, "Your profile and user data has been deleted successfully", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                                                            finish();
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(EditProfileActivity.this, "Didnt worked!", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });


                                    } catch (Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        button_verify_otp.setVisibility(View.VISIBLE);
                                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    button_verify_otp.setVisibility(View.VISIBLE);
                                    Toast.makeText(EditProfileActivity.this, "Enter valid OTP!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        button_verify_otp.setVisibility(View.VISIBLE);
                        Toast.makeText(EditProfileActivity.this, "VerifyOtp: Some Error Occurred!!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    button_verify_otp.setVisibility(View.VISIBLE);
                    Toast.makeText(EditProfileActivity.this, "Please enter otp first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        inputotp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputotp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputotp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputotp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    inputotp1.requestFocus();
                }
            }
        });
        inputotp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputotp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    inputotp2.requestFocus();
                }
            }
        });
        inputotp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputotp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    inputotp3.requestFocus();
                }
            }
        });
        inputotp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputotp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    inputotp4.requestFocus();
                }
            }
        });
        inputotp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    if (inputotp1 != null && inputotp2 != null && inputotp3 != null && inputotp4 != null && inputotp5 != null && inputotp6 != null) {
                        //button_verify_otp = view.findViewById(R.id.button_verify_otp);
                        button_verify_otp.performClick();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    inputotp5.requestFocus();
                }
            }
        });
        inputotp1.requestFocus();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }

    private void openImageChoser() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            uriImage = data.getData();
            TagImage = "newImage";
            update_profile_image.setImageURI(uriImage);
        }
    }

    private void showProfile() {

        edittext_email_editprofile.setEnabled(false);

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
        referenceProfile.child(authProfile.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null) {

                    fullName = readWriteUserDetails.fullName;
                    profileImage = readWriteUserDetails.profileImage;
                    gender = readWriteUserDetails.gender;
                    dob = readWriteUserDetails.dob;
                    dateJ = readWriteUserDetails.dateJoined;
                    email = readWriteUserDetails.email;

                    if (profileImage.equals("n/a")) {

                        update_profile_image.setImageResource(R.drawable.user);
                        // Glide.with(view.getContext()).load(R.drawable.user).centerCrop().into(profile_image);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Glide.with(EditProfileActivity.this).load(profileImage).placeholder(R.drawable.ic_user).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(update_profile_image);
                    }

                    if(email.equals("n/a")){
                        edittext_email_editprofile.setText(R.string.no_email_provided);
                        change_email.setText(R.string.add_email);
                        remove_email.setVisibility(View.GONE);
                    }else {
                        edittext_email_editprofile.setText(email);
                        imageView_verified_email.setVisibility(View.VISIBLE);
                    }
                    edittext_name_editprofile.setText(fullName);
                    edittext_dateofbirth_editprofile.setText(dob);

                    if (Objects.equals(gender, "Male")) {
                        radio_group_editprofile_gender.check(R.id.radio_male);
                    } else {
                        radio_group_editprofile_gender.check(R.id.radio_female);
                    }
                    referenceProfile.keepSynced(true);


                } else {
                    Toast.makeText(EditProfileActivity.this, "Error getting user info!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Error getting user info!!", Toast.LENGTH_SHORT).show();
            }
        });


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