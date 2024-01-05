package com.dmsskbm.agrigroww.admin.adapters;

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
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
import com.dmsskbm.agrigroww.admin.ReadWriteUserDetails;
import com.dmsskbm.agrigroww.admin.SentEmailActivity;
import com.dmsskbm.agrigroww.admin.WebViewActivity;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class ShowExpertsAdapter extends RecyclerView.Adapter<ShowExpertsAdapter.Holder> {

    ArrayList<ReadWriteUserDetails> showExpertAdapterArrayList;
    Context context;
    BottomSheetDialog bottomSheetDialog;
    FirebaseAuth authProfile;

    public ShowExpertsAdapter(ArrayList<ReadWriteUserDetails> showExpertAdapterArrayList, Context context) {
        this.showExpertAdapterArrayList = showExpertAdapterArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowExpertsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_expert_layout, parent, false);
        return new ShowExpertsAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowExpertsAdapter.Holder holder, int position) {

        authProfile = FirebaseAuth.getInstance();

        ReadWriteUserDetails readWriteUserDetails = showExpertAdapterArrayList.get(position);
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

        holder.recyclerView_relative_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                View bsView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, view.findViewById(R.id.bottom_sheet_scroll));

                KenBurnsView animatedImageBackground = bsView.findViewById(R.id.animatedImageBackground);
                ImageViewZoom identify_image = bsView.findViewById(R.id.identify_image);
                TextView users_name, users_dateJoined, users_userId, users_email, users_dob, users_gender, users_bannedStatus;
                users_name = bsView.findViewById(R.id.users_name);
                users_dateJoined = bsView.findViewById(R.id.users_dateJoined);
                users_userId = bsView.findViewById(R.id.users_userId);
                users_email = bsView.findViewById(R.id.users_email);
                users_dob = bsView.findViewById(R.id.users_dob);
                users_gender = bsView.findViewById(R.id.users_gender);
                users_bannedStatus = bsView.findViewById(R.id.users_bannedStatus);

                Glide.with(animatedImageBackground.getContext()).load(readWriteUserDetails.getProfileImage())
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(animatedImageBackground);

                Glide.with(identify_image.getContext()).load(readWriteUserDetails.getProfileImage())
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .error(R.drawable.ic_user)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(identify_image);

                users_name.setText(readWriteUserDetails.getFullName());
                users_userId.setText(readWriteUserDetails.getUserId());
                users_dateJoined.setText(readWriteUserDetails.getDateJoined());
                users_dob.setText(readWriteUserDetails.getDob());
                users_email.setText(readWriteUserDetails.getEmail());
                users_gender.setText(readWriteUserDetails.getGender());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userDetails").child(readWriteUserDetails.getUserId());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("isBanned")) {
                            users_bannedStatus.setText(R.string.yes);
                        } else {
                            users_bannedStatus.setText(R.string.no);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                bottomSheetDialog.setContentView(bsView);
                bottomSheetDialog.show();
            }
        });

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

                            if (readWriteUserDetails.getEmail().equals("n/a")) {
                                Toast.makeText(context, "User has no registered mail!", Toast.LENGTH_LONG).show();
                            } else {

                                Intent intent = new Intent(context, SentEmailActivity.class);
                                intent.putExtra("name", readWriteUserDetails.getFullName());
                                intent.putExtra("email", readWriteUserDetails.getEmail());
                                intent.putExtra("uid", readWriteUserDetails.getUserId());
                                context.startActivity(intent);

                            }
                        }
                        return false;
                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true);
                }


                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);


                popup.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return showExpertAdapterArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView show_name;
        ImageView person_imageView;
        ProgressBar progressBar;
        ImageButton three_dot_menu;
        RelativeLayout recyclerView_relative_btn;

        public Holder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            show_name = itemView.findViewById(R.id.show_name);
            person_imageView = itemView.findViewById(R.id.person_imageView);
            three_dot_menu = itemView.findViewById(R.id.three_dot_menu);
            recyclerView_relative_btn = itemView.findViewById(R.id.recyclerView_relative_btn);
        }
    }
}
