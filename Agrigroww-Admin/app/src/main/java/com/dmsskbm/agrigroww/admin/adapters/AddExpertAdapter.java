package com.dmsskbm.agrigroww.admin.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dmsskbm.agrigroww.admin.R;
import com.dmsskbm.agrigroww.admin.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddExpertAdapter extends RecyclerView.Adapter<AddExpertAdapter.Holder> {

    ArrayList<ReadWriteUserDetails> AddExpertAdapterArrayList;
    ArrayList<ReadWriteUserDetails> usersArrayList;
    Context context;
    FirebaseAuth authProfile;
    FirebaseDatabase firebaseDatabase;
    String userUID, reqNo;

    public AddExpertAdapter(ArrayList<ReadWriteUserDetails> AddExpertAdapterArrayList, Context context, String userUID, String reqNo) {
        this.AddExpertAdapterArrayList = AddExpertAdapterArrayList;
        this.context = context;
        this.userUID = userUID;
        this.reqNo = reqNo;
    }

    @NonNull
    @Override
    public AddExpertAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_expert_layout, parent, false);
        return new AddExpertAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddExpertAdapter.Holder holder, int position) {

        authProfile = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        ReadWriteUserDetails readWriteUserDetails = AddExpertAdapterArrayList.get(position);
        holder.show_name.setText(readWriteUserDetails.getFullName());
        Glide.with(holder.person_imageView.getContext()).load(readWriteUserDetails.getProfileImage()).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.ic_user)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.person_imageView);
        String totalReq = String.valueOf(readWriteUserDetails.getTotalRequests());
        holder.show_totalRequests.setText(totalReq);

        holder.recyclerView_relative_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.progressBarBs.setVisibility(View.VISIBLE);

                usersArrayList = new ArrayList<>();

                DatabaseReference databaseReference = firebaseDatabase.getReference().child("userDetails").child(userUID);
                DatabaseReference databaseReference1 = firebaseDatabase.getReference().child("ExpertDetails").child(readWriteUserDetails.getUserId());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetails readWriteUserDetails1 = snapshot.getValue(ReadWriteUserDetails.class);
                        usersArrayList.add(readWriteUserDetails1);
                        holder.progressBarBs.setVisibility(View.GONE);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
                        View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
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
                        String custom_text = "Are you sure, you want to add " + readWriteUserDetails.getFullName() + " to " + readWriteUserDetails1.getFullName() + "'s Request No " + reqNo;
                        custom_textview.setText(custom_text);
                        custom_textview.setVisibility(View.VISIBLE);

                        alertDialogBuilder.setView(view);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);

                        Log.e("reqNo",reqNo);
                        Log.e("userUID",userUID);


                        ok_button.setTextColor(context.getResources().getColor(R.color.red));
                        ok_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();
                                databaseReference.child("requests").child(reqNo).child("members").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            String members = snapshot.getValue(String.class);
                                            Log.e("members",members);

                                            if (!members.contains(readWriteUserDetails.getUserId())) {
                                                members = members + ", " + readWriteUserDetails.getUserId();
                                                databaseReference.child("requests").child(reqNo).child("members").setValue(members).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){
                                                            databaseReference1.child("totalRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        String totalReq = String.valueOf(snapshot.getValue(Long.class));
                                                                        int totReq = Integer.parseInt(totalReq);
                                                                        if (totReq > 0) {
                                                                            totReq = totReq + 1;
                                                                            databaseReference1.child("totalRequests").setValue(totReq);
                                                                        } else {
                                                                            totReq = 1;
                                                                            databaseReference1.child("totalRequests").setValue(totReq);
                                                                        }
                                                                    } else {
                                                                        int totReq = 1;
                                                                        databaseReference1.child("totalRequests").setValue(totReq);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                            String toast = readWriteUserDetails.getFullName() + " has been added to " + readWriteUserDetails1.getFullName() + "'s Request No " + reqNo;
                                                            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                                                            ((Activity) context).finish();
                                                        }

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "Failed to add expert! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else{
                                                Toast.makeText(context, readWriteUserDetails.getFullName()+", is already added to "+ readWriteUserDetails1.getFullName() + "'s Request No " + reqNo, Toast.LENGTH_SHORT).show();
                                            }
                                        }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return AddExpertAdapterArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView show_name, show_totalRequests;
        ImageView person_imageView;
        ProgressBar progressBar, progressBarBs;
        ImageButton three_dot_menu;
        RelativeLayout recyclerView_relative_btn;

        public Holder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            show_name = itemView.findViewById(R.id.show_name);
            person_imageView = itemView.findViewById(R.id.person_imageView);
            three_dot_menu = itemView.findViewById(R.id.three_dot_menu);
            recyclerView_relative_btn = itemView.findViewById(R.id.recyclerView_relative_btn);
            show_totalRequests = itemView.findViewById(R.id.show_totalRequests);
            progressBarBs = itemView.findViewById(R.id.progressBarBs);
        }
    }
}
