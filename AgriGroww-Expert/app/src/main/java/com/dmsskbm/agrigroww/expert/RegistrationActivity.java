package com.dmsskbm.agrigroww.expert;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Timer;
import java.util.TimerTask;

public class RegistrationActivity extends AppCompatActivity {

    ImageView register_profile_image;
    EditText edittext_name_signup, edittext_dateofbirth_signup, edittext_authentication_number_signup;
    RadioGroup radioGroupRegisterGender;
    RadioButton radioButtonRegisterGenderSelected;
    ProgressBar progressBar;
    Button button_continue;
    FirebaseStorage firebaseStorage;
    Uri uriImage;
    ImageButton change_language_btn;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatePickerDialog picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        register_profile_image = findViewById(R.id.register_profile_image);
        edittext_name_signup = findViewById(R.id.edittext_name_signup);
        edittext_dateofbirth_signup = findViewById(R.id.edittext_dateofbirth_signup);
        edittext_authentication_number_signup = findViewById(R.id.edittext_authentication_number_signup);
        progressBar = findViewById(R.id.progressBar);
        button_continue = findViewById(R.id.button_continue);
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();
        change_language_btn = findViewById(R.id.change_language_btn);

        //change language button
        change_language_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent intent = new Intent(RegistrationActivity.this, ChangeLanguageActivity.class);
               // startActivity(intent);
            }
        });


        register_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChoser();
                if (edittext_name_signup.hasFocus()) {
                    edittext_name_signup.clearFocus();
                }
            }
        });
        edittext_dateofbirth_signup.setFocusable(false);
        edittext_dateofbirth_signup.setCursorVisible(false);
        edittext_dateofbirth_signup.setClickable(true);
        edittext_dateofbirth_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                if (edittext_name_signup.hasFocus()) {
                    edittext_name_signup.clearFocus();
                }

                //date picker dialog
                picker = new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        edittext_dateofbirth_signup.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

                    }
                }, year, month, day);
                final Calendar cmax = Calendar.getInstance();
                cmax.set(2003, 01, 01);
                picker.getDatePicker().setMaxDate(cmax.getTimeInMillis());
                picker.show();
            }
        });

        //continue button
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edittext_name_signup.hasFocus()) {
                    edittext_name_signup.clearFocus();
                }

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textFullName = edittext_name_signup.getText().toString();
                String textDoB = edittext_dateofbirth_signup.getText().toString();
                String textAuthenticationNumber = edittext_authentication_number_signup.getText().toString().trim();
                String textGender;

                //checking edittext error and format
                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your full name!", Toast.LENGTH_LONG).show();
                    edittext_name_signup.requestFocus();
                } else if (textAuthenticationNumber.length() != 8) {
                    Toast.makeText(RegistrationActivity.this, "Please enter 8 Characters Authentication Number!", Toast.LENGTH_LONG).show();
                    edittext_authentication_number_signup.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegistrationActivity.this, "Please select your Date of Birth!", Toast.LENGTH_LONG).show();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegistrationActivity.this, "Please select your Gender", Toast.LENGTH_LONG).show();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();

                    edittext_name_signup.setCursorVisible(false);
                    progressBar.setVisibility(View.VISIBLE);
                    button_continue.setVisibility(View.INVISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Extras");
                    referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String expertAuthKey = snapshot.child("ExpertRegistrationKey").getValue(String.class);
                                if (expertAuthKey != null && expertAuthKey.equals(textAuthenticationNumber)) {
                                    registerUser(textFullName, textDoB, textGender);
                                } else {
                                    edittext_name_signup.setCursorVisible(true);
                                    progressBar.setVisibility(View.GONE);
                                    button_continue.setVisibility(View.VISIBLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(RegistrationActivity.this, "Enter Correct Expert Authentication Key!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Error in Expert authentication!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });

    }

    //selecting image
    private void openImageChoser() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE_REQUEST);
    }

    //checking if image is selected
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            uriImage = data.getData();
            register_profile_image.setImageURI(uriImage);
        }
    }

    private void registerUser(String textFullName, String textDoB, String textGender) {

        FirebaseAuth authProfile = FirebaseAuth.getInstance();

        if (uriImage != null) {

            DatabaseReference referenceDelete = FirebaseDatabase.getInstance().getReference("userDetails").child(authProfile.getCurrentUser().getUid());
            referenceDelete.removeValue();

            firebaseStorage = FirebaseStorage.getInstance();

            //uploading profile picture to firebase storage
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
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String textDateJoined = sdf1.format(new Date());
                            String textEmail = "n/a";
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDoB, textGender, textProfileImage, textPhone, textDateJoined, textUserId, textEmail);
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ExpertDetails");
                            referenceProfile.child(authProfile.getCurrentUser().getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        progressBar.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        final AlertDialog.Builder account_created = new AlertDialog.Builder(RegistrationActivity.this, R.style.RoundedCornersDialog);
                                        View view = LayoutInflater.from(RegistrationActivity.this).inflate(R.layout.custom_alert_dialog, null);
                                        account_created.setView(view);
                                        AlertDialog alertDialog = account_created.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();
                                        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                        final Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {

                                                /*if(textEmail != "n/a"){
                                                    alertDialog.dismiss();
                                                    timer.cancel();
                                                    Intent intent = new Intent(RegistrationActivity.this, EmailVerificationActivity.class);
                                                    intent.putExtra("email",textEmail);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                                                    finish();
                                                }else {*/
                                                alertDialog.dismiss();
                                                timer.cancel();
                                                Intent intent = new Intent(RegistrationActivity.this, EmailVerificationActivity.class);
                                                intent.putExtra("extra", "fromRegistration");
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                                                finish();

                                            }
                                        }, 4000);

                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            });
        } else {


            DatabaseReference referenceDelete = FirebaseDatabase.getInstance().getReference("userDetails").child(authProfile.getCurrentUser().getUid());
            referenceDelete.removeValue();

            String textProfileImage = "n/a";
            String textPhone = authProfile.getCurrentUser().getPhoneNumber();
            String textUserId = authProfile.getCurrentUser().getUid();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String textDateJoined = sdf1.format(new Date());
            String textEmail = "n/a";
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDoB, textGender, textProfileImage, textPhone, textDateJoined, textUserId, textEmail);
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
            referenceProfile.child(authProfile.getCurrentUser().getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        final AlertDialog.Builder account_created = new AlertDialog.Builder(RegistrationActivity.this, R.style.RoundedCornersDialog);
                        View view = LayoutInflater.from(RegistrationActivity.this).inflate(R.layout.custom_alert_dialog, null);
                        account_created.setView(view);
                        AlertDialog alertDialog = account_created.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                alertDialog.dismiss();
                                timer.cancel();
                                Intent intent = new Intent(RegistrationActivity.this, EmailVerificationActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                                finish();

                            }
                        }, 4000);

                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}