package com.dmsskbm.agrigroww.admin.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.dmsskbm.agrigroww.admin.ReadWriteAdminDetails;
import com.dmsskbm.agrigroww.admin.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowBannedUsersAdapter extends RecyclerView.Adapter<ShowBannedUsersAdapter.Holder>{

    ArrayList<ReadWriteUserDetails> showBannedUsersAdapterArrayList;
    Context context;
    FirebaseAuth authProfile;

    public ShowBannedUsersAdapter(ArrayList<ReadWriteUserDetails> showBannedUsersAdapterArrayList, Context context) {
        this.showBannedUsersAdapterArrayList = showBannedUsersAdapterArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowBannedUsersAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_expert_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowBannedUsersAdapter.Holder holder, int position) {

        authProfile = FirebaseAuth.getInstance();

        ReadWriteUserDetails readWriteUserDetails = showBannedUsersAdapterArrayList.get(position);
        holder.show_name.setText(readWriteUserDetails.getFullName());
        String reqNo = String.valueOf(readWriteUserDetails.getTotalRequests());
        holder.show_totalRequests.setText(reqNo);
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

        holder.three_dot_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(context, R.style.CustomPopupTheme);
                PopupMenu popup = new PopupMenu(wrapper, holder.three_dot_menu);
                popup.inflate(R.menu.user_experts_popup_menu);

                Menu menu = popup.getMenu();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userDetails");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.three_dot_email) {



                        } else if (menuItem.getItemId() == R.id.three_dot_ban) {


                            AlertDialog.Builder logout = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
                            View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                            Button ok_button = view.findViewById(R.id.ok_button);
                            Button cancel_button = view.findViewById(R.id.cancel_button);
                            CheckBox custom_checkbox = view.findViewById(R.id.custom_checkbox);
                            cancel_button.setVisibility(View.VISIBLE);
                            ok_button.setVisibility(View.VISIBLE);
                            custom_checkbox.setVisibility(View.VISIBLE);

                            LottieAnimationView custom_animationView = view.findViewById(R.id.custom_animationView);
                            custom_animationView.setAnimation("red_warning.json");
                            custom_animationView.setVisibility(View.VISIBLE);
                            custom_animationView.setRepeatCount(LottieDrawable.INFINITE);

                            if(readWriteUserDetails.getEmail().equals("n/a")){
                                custom_checkbox.setText(R.string.send_Email_to_user_user_has_no_email);
                                custom_checkbox.setEnabled(false);
                            }else{
                                custom_checkbox.setText(R.string.send_email_to_user);
                            }

                            int width = 800;
                            int height = 600;
                            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                            parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            custom_animationView.setLayoutParams(parms);

                            TextView custom_textview = view.findViewById(R.id.custom_textview);
                            String custom_text = "Are you sure, you want to ban "+ readWriteUserDetails.getFullName()+"?"; //context.getString(R.string.logging_out);
                            custom_textview.setText(custom_text);
                            custom_textview.setVisibility(View.VISIBLE);

                            logout.setView(view);
                            AlertDialog alertDialog = logout.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            ok_button.setTextColor(context.getResources().getColor(R.color.red));
                            ok_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    databaseReference.child(readWriteUserDetails.getUserId()).child("isBanned").setValue("YES").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            alertDialog.dismiss();
                                            Toast.makeText(context, readWriteUserDetails.getFullName() + " has been banned!", Toast.LENGTH_SHORT).show();
                                            if(custom_checkbox.isChecked()){
                                                banUserEmail(readWriteUserDetails);
                                            }
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

                        } else if (menuItem.getItemId() == R.id.three_dot_Unban) {

                            AlertDialog.Builder logout = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
                            View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                            Button ok_button = view.findViewById(R.id.ok_button);
                            Button cancel_button = view.findViewById(R.id.cancel_button);
                            CheckBox custom_checkbox = view.findViewById(R.id.custom_checkbox);
                            cancel_button.setVisibility(View.VISIBLE);
                            ok_button.setVisibility(View.VISIBLE);
                            custom_checkbox.setVisibility(View.VISIBLE);

                            LottieAnimationView custom_animationView = view.findViewById(R.id.custom_animationView);
                            custom_animationView.setAnimation("green_tick.json");
                            custom_animationView.setVisibility(View.VISIBLE);

                            if(readWriteUserDetails.getEmail().equals("n/a")){
                                custom_checkbox.setText(R.string.send_Email_to_user_user_has_no_email);
                                custom_checkbox.setEnabled(false);
                            }else{
                                custom_checkbox.setText(R.string.send_email_to_user);
                            }

                            int width = 800;
                            int height = 600;
                            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                            parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            custom_animationView.setLayoutParams(parms);

                            TextView custom_textview = view.findViewById(R.id.custom_textview);
                            String custom_text = "Are you sure, you want to Unban "+ readWriteUserDetails.getFullName()+"?"; //context.getString(R.string.logging_out);
                            custom_textview.setText(custom_text);
                            custom_textview.setVisibility(View.VISIBLE);

                            logout.setView(view);
                            AlertDialog alertDialog = logout.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            ok_button.setTextColor(context.getResources().getColor(R.color.main_color));
                            ok_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    databaseReference.child(readWriteUserDetails.getUserId()).child("isBanned").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            alertDialog.dismiss();
                                            Toast.makeText(context, readWriteUserDetails.getFullName() + " has been Unbanned!", Toast.LENGTH_SHORT).show();
                                            if(custom_checkbox.isChecked()){
                                                UnbanUserEmail(readWriteUserDetails);
                                            }
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

                        return false;
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true);
                }

                databaseReference.child(readWriteUserDetails.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("isBanned")){
                            menu.getItem(1).setEnabled(false);
                            menu.getItem(2).setEnabled(true);
                        } else {
                            menu.getItem(1).setEnabled(true);
                            menu.getItem(2).setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                popup.show();
            }
        });


    }

    private void UnbanUserEmail(ReadWriteUserDetails readWriteUserDetails) {
    }

    private void banUserEmail(ReadWriteUserDetails readWriteUserDetails) {
        
    }

    @Override
    public int getItemCount() {
        return showBannedUsersAdapterArrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView show_name, show_totalRequests;
        ImageView person_imageView;
        ProgressBar progressBar;
        ImageButton three_dot_menu;
        public Holder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            show_name = itemView.findViewById(R.id.show_name);
            show_totalRequests = itemView.findViewById(R.id.show_totalRequests);
            person_imageView = itemView.findViewById(R.id.person_imageView);
            three_dot_menu  = itemView.findViewById(R.id.three_dot_menu);
        }
    }
}
