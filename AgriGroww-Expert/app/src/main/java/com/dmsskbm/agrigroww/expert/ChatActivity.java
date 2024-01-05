package com.dmsskbm.agrigroww.expert;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.dmsskbm.agrigroww.expert.adapters.MessagesAdapter;
import com.dmsskbm.agrigroww.expert.extras.AudioPermissions;
import com.dmsskbm.agrigroww.expert.extras.KeyboardUtils;
import com.dmsskbm.agrigroww.expert.models.ModelLoadRequests;
import com.dmsskbm.agrigroww.expert.models.ModelMessage;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    MessagesAdapter messagesAdapter;
    ArrayList<ModelMessage> messageArrayList;
    ArrayList<ReadWriteUserDetails> readWriteUserDetailsArrayList;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    RelativeLayout relative_bottom_area;
    ImageButton back_arrow_btn, main_chat_send_btn, chat_add_media_btn, add_image_btn, add_video_btn;
    CardView card_add_media, card_add_media1;
    TextView textView_usersRequestNo, textview_request_marked_solved;
    RecyclerView messages_recyclerView;
    EditText editMessage;
    RecordButton record_button;
    RecordView record_view;
    MediaRecorder mediaRecorder;
    private String audioPath;
    ProgressDialog progressDialog;
    Long savedReq;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    String userUID, expertUID, groupName, savedName, solved;
    int reqNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back_arrow_btn = findViewById(R.id.back_arrow_btn);
        main_chat_send_btn = findViewById(R.id.main_chat_send_btn);
        chat_add_media_btn = findViewById(R.id.chat_add_media_btn);
        textView_usersRequestNo = findViewById(R.id.textView_usersRequestNo);
        textview_request_marked_solved = findViewById(R.id.textview_request_marked_solved);
        messages_recyclerView = findViewById(R.id.messages_recyclerView);
        relative_bottom_area = findViewById(R.id.relative_bottom_area);
        add_image_btn = findViewById(R.id.add_image_btn);
        add_video_btn = findViewById(R.id.add_video_btn);
        card_add_media = findViewById(R.id.card_add_media);
        card_add_media1 = findViewById(R.id.card_add_media1);
        editMessage = findViewById(R.id.editMessage);
        record_button = findViewById(R.id.record_button);
        record_view = findViewById(R.id.record_view);

        progressDialog = new ProgressDialog(ChatActivity.this);

        messageArrayList = new ArrayList<>();
        readWriteUserDetailsArrayList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messageArrayList);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        expertUID = FirebaseAuth.getInstance().getUid();


        messages_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages_recyclerView.setAdapter(messagesAdapter);


        SharedPreferences options = ChatActivity.this.getSharedPreferences("ExpertDetails", Context.MODE_PRIVATE);
        savedName = options.getString("name", "");


        groupName = getIntent().getStringExtra("groupName");
        userUID = getIntent().getStringExtra("usersUID");
        solved = getIntent().getStringExtra("solved");

        if(solved.equals("yes")){
            relative_bottom_area.setVisibility(View.GONE);
            textview_request_marked_solved.setVisibility(View.VISIBLE);
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
                messagesAdapter.notifyDataSetChanged();
                //messagesAdapter.notifyItemChanged(messagesAdapter.getItemCount() - 1);
                messages_recyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        main_chat_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messageTxt = editMessage.getText().toString().trim();

                if (!messageTxt.equals("")) {
                    editMessage.setText("");

                    Date date = new Date();
                    //ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
                    String messageType = "text";
                    ModelMessage message = new ModelMessage(messageTxt, expertUID, date.getTime(), savedName, "NO", "NO", "YES", messageType);
                    if (savedReq != null) {
                        firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(savedReq)).push().setValue(message);
                    } else {
                        firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(reqNo)).push().setValue(message);
                    }


                }

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
                openImageChoser();
            }
        });
        add_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card_add_media.setVisibility(View.GONE);
                card_add_media1.setVisibility(View.GONE);
                openVideoChoser();
            }
        });


        //used to pull up the recyclerview
        KeyboardUtils.addKeyboardToggleListener(ChatActivity.this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                messages_recyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        });

        record_button.setRecordView(record_view);
        record_button.setListenForRecord(false);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AudioPermissions.isRecordingOk(ChatActivity.this)) {
                    record_button.setListenForRecord(true);
                } else {
                    AudioPermissions.requestRecording(ChatActivity.this);
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
                            String audioPath = uri.toString();
                            progressDialog.dismiss();
                            Date date = new Date();
                            //ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
                            String messageType = "audio";
                            String messageTxt = "audio";

                            ModelMessage message = new ModelMessage(messageTxt, expertUID, date.getTime(), savedName, "NO", "NO", "YES", messageType);
                            message.setDataUrl(audioPath);

                            firebaseDatabase.getReference().child("userChats").child(userUID).child(String.valueOf(reqNo)).push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(ChatActivity.this, "Audio Sent Successfully!", Toast.LENGTH_SHORT).show();
                                    }else {
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
        File file  = new File(recordPath, System.currentTimeMillis() + ".acc");
        return file.getPath();
    }


    private void openImageChoser() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1920)
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
                                    //ReadWriteUserDetails readWriteUserDetails = readWriteUserDetailsArrayList.get(0);
                                    String messageType = "photo";
                                    String messageTxt = "photo";
                                    Date date = new Date();

                                    ModelMessage message = new ModelMessage(messageTxt, expertUID, date.getTime(), savedName, "NO", "NO", "YES", messageType);
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
                                    ModelMessage message = new ModelMessage(messageTxt, expertUID, date.getTime(), savedName, "NO", "NO", "YES", messageType);
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


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
}