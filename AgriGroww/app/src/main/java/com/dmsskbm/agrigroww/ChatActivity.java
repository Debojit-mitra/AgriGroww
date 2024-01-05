package com.dmsskbm.agrigroww;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.dmsskbm.agrigroww.adapters.MessagesAdapter;
import com.dmsskbm.agrigroww.extras.AudioPermissions;
import com.dmsskbm.agrigroww.extras.KeyboardUtils;
import com.dmsskbm.agrigroww.models.ModelLoadRequests;
import com.dmsskbm.agrigroww.models.ModelMessage;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    MessagesAdapter messagesAdapter;
    ArrayList<ModelMessage> messageArrayList;
    ArrayList<ReadWriteUserDetails> readWriteUserDetailsArrayList;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    ImageButton three_dot_menu;
    ImageButton back_arrow_btn, main_chat_send_btn, chat_add_media_btn, add_image_btn, add_video_btn;
    CardView card_add_media, card_add_media1;
    TextView textView_usersRequestNo, textview_request_marked_solved;
    ProgressDialog progressDialog;
    RecyclerView messages_recyclerView;
    NestedScrollView messages_nestedScrollView;
    RelativeLayout textview_layout, relative_bottom_area, relative_bottom_area2;
    EditText editMessage;
    Long totalRequests, savedReq;
    RecordButton record_button;
    RecordView record_view;
    MediaRecorder mediaRecorder;
    private String audioPath;
    String userUID, groupName, solved, checkButton, savedName, members;
    int reqNo;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    SharedPreferences options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back_arrow_btn = findViewById(R.id.back_arrow_btn);
        main_chat_send_btn = findViewById(R.id.main_chat_send_btn);
        textView_usersRequestNo = findViewById(R.id.textView_usersRequestNo);
        textview_request_marked_solved = findViewById(R.id.textview_request_marked_solved);
        messages_recyclerView = findViewById(R.id.messages_recyclerView);
        messages_nestedScrollView = findViewById(R.id.messages_nestedScrollView);
        chat_add_media_btn = findViewById(R.id.chat_add_media_btn);
        add_image_btn = findViewById(R.id.add_image_btn);
        add_video_btn = findViewById(R.id.add_video_btn);
        card_add_media = findViewById(R.id.card_add_media);
        card_add_media1 = findViewById(R.id.card_add_media1);
        editMessage = findViewById(R.id.editMessage);
        textview_layout = findViewById(R.id.textview_layout);
        relative_bottom_area = findViewById(R.id.relative_bottom_area);
        relative_bottom_area2 = findViewById(R.id.relative_bottom_area2);
        three_dot_menu = findViewById(R.id.three_dot_menu);
        record_button = findViewById(R.id.record_button);
        record_view = findViewById(R.id.record_view);
        progressDialog = new ProgressDialog(ChatActivity.this);

        messageArrayList = new ArrayList<>();
        readWriteUserDetailsArrayList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messageArrayList);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        userUID = FirebaseAuth.getInstance().getUid();

        options = ChatActivity.this.getSharedPreferences("checkButtonPressed", Context.MODE_PRIVATE);
        checkButton = options.getString("NewChatButtonPressed", "NO");


        messages_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //messages_recyclerView.setNestedScrollingEnabled(false);
        //messages_nestedScrollView.setNestedScrollingEnabled(false);
       // messagesAdapter.setHasStableIds(true); //to decrease lag in recyclerView
        messages_recyclerView.setHasFixedSize(false);
        messages_recyclerView.setAdapter(messagesAdapter);


        SharedPreferences options = ChatActivity.this.getSharedPreferences("usersDetails", Context.MODE_PRIVATE);
        savedName = options.getString("name", "");

        if (checkButton.equals("NO")) {

            groupName = getIntent().getStringExtra("groupName");
            solved = getIntent().getStringExtra("solved");

            if (solved.equals("yes")) {

                relative_bottom_area.setVisibility(View.GONE);
                textview_request_marked_solved.setVisibility(View.VISIBLE);

            } else {
                three_dot_menu.setVisibility(View.VISIBLE);
            }

            textView_usersRequestNo.setText(groupName);
            reqNo = Integer.parseInt(groupName.replaceAll("[\\D]", ""));
            firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(reqNo)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messageArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ModelMessage message = dataSnapshot.getValue(ModelMessage.class);
                        message.setMessageId(dataSnapshot.getKey());
                        messageArrayList.add(message);
                    }
                    //messagesAdapter.notifyDataSetChanged();
                    try {
                        messagesAdapter.notifyItemChanged(messagesAdapter.getItemCount() - 1);
                        messages_recyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   // messages_recyclerView.scheduleLayoutAnimation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            three_dot_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popup = new PopupMenu(ChatActivity.this, three_dot_menu);
                    popup.getMenuInflater().inflate(R.menu.chat_activity_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            if (menuItem.getItemId() == R.id.tab_mark_as_solved) {


                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this, R.style.RoundedCornersDialog);
                                View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alert_dialog, null);
                                Button ok_button = view.findViewById(R.id.ok_button);
                                Button cancel_button = view.findViewById(R.id.cancel_button);
                                cancel_button.setVisibility(View.VISIBLE);
                                ok_button.setVisibility(View.VISIBLE);

                                LottieAnimationView custom_animationView = view.findViewById(R.id.custom_animationView);
                                custom_animationView.setAnimation("green_tick.json");
                                custom_animationView.setVisibility(View.VISIBLE);
                                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);

                                int width = 800;
                                int height = 600;
                                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                custom_animationView.setLayoutParams(parms);

                                TextView custom_textview;
                                custom_textview = view.findViewById(R.id.custom_textview);
                                String custom_text = "Please confirm, you want to mark Request No. " + reqNo + " as solved!";
                                custom_textview.setText(custom_text);
                                custom_textview.setVisibility(View.VISIBLE);

                                alertDialogBuilder.setView(view);
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                ok_button.setTextColor(ChatActivity.this.getResources().getColor(R.color.main_color));
                                ok_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        DatabaseReference databaseReferenceMarkSolved = firebaseDatabase.getReference("userDetails").child(userUID)
                                                .child("requests").child(String.valueOf(reqNo)).child("solved");
                                        databaseReferenceMarkSolved.setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    relative_bottom_area.setVisibility(View.GONE);
                                                    textview_request_marked_solved.setVisibility(View.VISIBLE);
                                                    Toast.makeText(ChatActivity.this, "Request No. " + reqNo + " has been marked solved!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChatActivity.this, "Could`nt mark request No. " + reqNo + " as solved!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        alertDialog.dismiss();
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

                            return false;
                        }
                    });

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true);
                }*/

                    popup.show();

                }
            });


        } else if (checkButton.equals("YES")) {

            members = userUID;
            firebaseDatabase.getReference().child("userDetails").child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    int req = (int) (readWriteUserDetails.getTotalRequests() + 1);
                    String reqNo = "Request #" + req;
                    textView_usersRequestNo.setText(reqNo);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        main_chat_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messageTxt = editMessage.getText().toString().trim();

                if (!messageTxt.equals("")) {
                    editMessage.setText("");


                    if (checkButton.equals("YES")) {

                        AlertDialog.Builder loading = new AlertDialog.Builder(ChatActivity.this, R.style.RoundedCornersDialog);
                        View progressDialogView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alert_dialog, null);
                        LottieAnimationView custom_animationView = progressDialogView.findViewById(R.id.custom_animationView);
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
                        loading.setView(progressDialogView);
                        AlertDialog alertDialog = loading.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        alertDialog.getWindow().setLayout(400, 400);

                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
                        referenceProfile.child(userUID).child("totalRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    totalRequests = snapshot.getValue(Long.class);
                                    long totalReq = Long.valueOf(totalRequests) + 1;
                                    referenceProfile.child(userUID).child("totalRequests").setValue(totalReq);
                                    String groupName = "Request #" + totalReq;
                                    Date date = new Date();


                                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("AdminDetails");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // readWriteAdminDetailsArrayList.clear();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                                                //  readWriteAdminDetailsArrayList.add(readWriteUserDetails);
                                                members = members + ", " + readWriteUserDetails.getUserId();
                                            }
                                            solved = "no";
                                            ModelLoadRequests modelLoadRequests = new ModelLoadRequests(groupName, members, date.getTime(), solved);
                                            referenceProfile.child(userUID).child("requests").child(String.valueOf(totalReq)).setValue(modelLoadRequests);
                                            referenceProfile.child(userUID).child("latestRequestTime").setValue(date.getTime());
                                            createGroup(totalReq, messageTxt, alertDialog);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                } else {
                                    referenceProfile.child(userUID).child("totalRequests").setValue(1);
                                    totalRequests = 0L;
                                    long totalReq = 1L;
                                    String groupName = "Request #" + totalReq;


                                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("AdminDetails");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // readWriteAdminDetailsArrayList.clear();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                                                //  readWriteAdminDetailsArrayList.add(readWriteUserDetails);
                                                members = members + ", " + readWriteUserDetails.getUserId();
                                            }

                                            Date date = new Date();
                                            solved = "no";
                                            ModelLoadRequests modelLoadRequests = new ModelLoadRequests(groupName, members, date.getTime(), solved);
                                            referenceProfile.child(userUID).child("requests").child(String.valueOf(totalReq)).setValue(modelLoadRequests);
                                            referenceProfile.child(userUID).child("latestRequestTime").setValue(date.getTime());
                                            createGroup(totalReq, messageTxt, alertDialog);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else if (checkButton.equals("NO")) {

                        Date date = new Date();
                        //ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
                        String messageType = "text";
                        ModelMessage message = new ModelMessage(messageTxt, userUID, date.getTime(), savedName, "YES", "NO", "NO", messageType);
                        if (savedReq != null) {
                            firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(savedReq)).push().setValue(message);
                        } else {
                            firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(reqNo)).push().setValue(message);
                        }


                    }


                }

            }
        });

        //checking whether expert has been added to chat
        DatabaseReference databaseReferenceForMembersCheck = firebaseDatabase.getReference("userDetails").child(userUID)
                .child("requests").child(String.valueOf(reqNo)).child("members");
        databaseReferenceForMembersCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String members = snapshot.getValue(String.class);

                    DatabaseReference databaseReference = firebaseDatabase.getReference("ExpertDetails");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String key = dataSnapshot.getKey();
                                    if (members.contains(key)) {
                                        textview_layout.setVisibility(View.VISIBLE);
                                        return;
                                    }
                                }
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
            }
        });

        chat_add_media_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!card_add_media.isShown() && !card_add_media1.isShown()){
                    card_add_media.setVisibility(View.VISIBLE);
                    card_add_media1.setVisibility(View.VISIBLE);
                }else{
                    card_add_media.setVisibility(View.GONE);
                    card_add_media1.setVisibility(View.GONE);
                }
            }
        });

        add_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card_add_media.setVisibility(View.GONE);
                card_add_media1.setVisibility(View.GONE);
                if (checkButton.equals("NO")) {

                    openImageChoser();

                } else if (checkButton.equals("YES")) {
                    Toast.makeText(ChatActivity.this, "Please send a text first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        add_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card_add_media.setVisibility(View.GONE);
                card_add_media1.setVisibility(View.GONE);
                if (checkButton.equals("NO")) {

                    openVideoChoser();

                } else if (checkButton.equals("YES")) {
                    Toast.makeText(ChatActivity.this, "Please send a text first!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        record_button.setRecordView(record_view);
        record_button.setListenForRecord(false);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkButton.equals("NO")) {

                    if (AudioPermissions.isRecordingOk(ChatActivity.this)) {
                        record_button.setListenForRecord(true);
                    } else {
                        AudioPermissions.requestRecording(ChatActivity.this);
                    }

                } else if (checkButton.equals("YES")) {
                    Toast.makeText(ChatActivity.this, "Please send a text first!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        record_view.setCancelBounds(80);
        record_view.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                Log.d("RecordView", "onStart");
                relative_bottom_area.setVisibility(View.GONE);
                setUpRecording();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                Log.d("RecordView", "onCancel");
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()) {
                    file.delete();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        relative_bottom_area.setVisibility(View.VISIBLE);
                    }
                }, 1000);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                Log.d("RecordView", "onFinish");
                mediaRecorder.stop();
                mediaRecorder.release();
                relative_bottom_area.setVisibility(View.VISIBLE);
                sendRecordingMessage();
            }

            @Override
            public void onLessThanSecond() {
                Log.d("RecordView", "onLessThanSecond");
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()) {
                    file.delete();
                }
                Toast.makeText(ChatActivity.this, "You need to record for more than a second!", Toast.LENGTH_SHORT).show();
                relative_bottom_area.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLock() {
                Log.d("RecordView", "onLock");
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()) {
                    file.delete();
                }
                Toast.makeText(ChatActivity.this, "Please record without locking!", Toast.LENGTH_SHORT).show();
                relative_bottom_area.setVisibility(View.VISIBLE);
            }
        });

        record_view.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                relative_bottom_area.setVisibility(View.VISIBLE);
            }
        });


        //used to pull up the recyclerview
        KeyboardUtils.addKeyboardToggleListener(ChatActivity.this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                messages_recyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        });


    }

    private void openVideoChoser() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        //long maxVideoSize = 24 * 1024 * 1024; // 10 MB
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,30);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
        Toast.makeText(this, "Max video duration 30 Seconds!", Toast.LENGTH_SHORT).show();

    }

    private void sendRecordingMessage() {
        progressDialog.setMessage("Uploading Audio!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        StorageReference reference = firebaseStorage.getReference("userChats").child(userUID).child("requests").child(String.valueOf(reqNo)).child(String.valueOf(calendar.getTimeInMillis()));
        Uri audioUri = Uri.fromFile(new File(audioPath));

        reference.putFile(audioUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imagePath = uri.toString();
                            progressDialog.dismiss();
                            Date date = new Date();
                            //ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
                            String messageType = "audio";
                            String messageTxt = "audio";
                            ModelMessage message = new ModelMessage(messageTxt, userUID, date.getTime(), savedName, "YES", "NO", "NO", messageType);
                            message.setDataUrl(imagePath);
                            if (savedReq != null) {
                                firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(savedReq)).push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ChatActivity.this, "Audio Sent Successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ChatActivity.this, "Audio Sent failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ChatActivity.this, "Audio Sent failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(reqNo)).push().setValue(message);
                            }

                        }
                    });
                }
            }
        });

    }

    private void setUpRecording() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioPath = getFilePath();
        mediaRecorder.setOutputFile(audioPath);
    }

    private String getFilePath() {
        ContextWrapper contextwrapper = new ContextWrapper(getApplicationContext());
        File recordPath = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            recordPath = contextwrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        }
        File file = new File(recordPath, System.currentTimeMillis() + ".acc");
        return file.getPath();
    }

    private void openImageChoser() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (data != null && data.getData() != null) {
                progressDialog.setMessage("Uploading Image!");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Uri selectedImage = data.getData();
                Calendar calendar = Calendar.getInstance();
                StorageReference reference = firebaseStorage.getReference("userChats").child(userUID).child("requests").child(String.valueOf(reqNo)).child(String.valueOf(calendar.getTimeInMillis()));
                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imagePath = uri.toString();
                                    progressDialog.dismiss();
                                    Date date = new Date();
                                    //ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
                                    String messageType = "photo";
                                    String messageTxt = "photo";
                                    ModelMessage message = new ModelMessage(messageTxt, userUID, date.getTime(), savedName, "YES", "NO", "NO", messageType);
                                    message.setDataUrl(imagePath);
                                    if (savedReq != null) {
                                        firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(savedReq)).push().setValue(message);
                                    } else {
                                        firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(reqNo)).push().setValue(message);
                                    }

                                }
                            });
                        }
                    }
                });
            }
        }


        if (requestCode == PICK_VIDEO_REQUEST) {
            if (data != null && data.getData() != null) {
                progressDialog.setMessage("Uploading Video!");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Uri selectedVideo = data.getData();
                Calendar calendar = Calendar.getInstance();
                StorageReference reference = firebaseStorage.getReference("userChats").child(userUID).child("requests").child(String.valueOf(reqNo)).child(String.valueOf(calendar.getTimeInMillis()));
                reference.putFile(selectedVideo).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String videoPath = uri.toString();
                                    progressDialog.dismiss();
                                    Date date = new Date();
                                    //ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
                                    String messageType = "video";
                                    String messageTxt = "video";
                                    ModelMessage message = new ModelMessage(messageTxt, userUID, date.getTime(), savedName, "YES", "NO", "NO", messageType);
                                    message.setDataUrl(videoPath);
                                    if (savedReq != null) {
                                        firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(savedReq)).push().setValue(message);
                                    } else {
                                        firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(reqNo)).push().setValue(message);
                                    }

                                }
                            });
                        }
                    }
                });

            }
        }


    }

    private void createGroup(Long totalReq, String messageTxt, AlertDialog alertDialog) {


        alertDialog.dismiss();

        Date date = new Date();
        //  ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
        String messageType = "text";
        ModelMessage message = new ModelMessage(messageTxt, userUID, date.getTime(), savedName, "YES", "NO", "NO", messageType);

        firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(totalReq)).push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    savedReq = totalReq;
                    options.edit().putString("NewChatButtonPressed", "NO").apply();
                    checkButton = "NO";

                    firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(totalReq)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messageArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ModelMessage message = dataSnapshot.getValue(ModelMessage.class);
                                message.setMessageId(dataSnapshot.getKey());
                                messageArrayList.add(message);
                            }
                            messagesAdapter.notifyDataSetChanged();
                            //messagesAdapter.notifyItemChanged(messagesAdapter.getItemCount()-1);
                            messages_recyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        options.edit().putString("NewChatButtonPressed", "NO").apply();
        super.onBackPressed();
        finish();
    }
}