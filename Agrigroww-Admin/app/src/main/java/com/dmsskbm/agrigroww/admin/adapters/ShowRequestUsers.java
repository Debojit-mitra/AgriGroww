package com.dmsskbm.agrigroww.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dmsskbm.agrigroww.admin.R;
import com.dmsskbm.agrigroww.admin.ReadWriteUserDetails;
import com.dmsskbm.agrigroww.admin.SentEmailActivity;
import com.dmsskbm.agrigroww.admin.ShowAllRequestsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowRequestUsers extends RecyclerView.Adapter<ShowRequestUsers.ShowRequestUsersViewHolder> {

    Context context;
    ArrayList<ReadWriteUserDetails> readRequestUserDetailsArraylist;

    public ShowRequestUsers(Context context, ArrayList<ReadWriteUserDetails> readRequestUserDetailsArraylist) {
        this.context = context;
        this.readRequestUserDetailsArraylist = readRequestUserDetailsArraylist;
    }

    @NonNull
    @Override
    public ShowRequestUsers.ShowRequestUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_expert_layout, parent, false);
        return new ShowRequestUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowRequestUsers.ShowRequestUsersViewHolder holder, int position) {

        ReadWriteUserDetails readWriteUserDetails = readRequestUserDetailsArraylist.get(position);
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

        String reqNo = String.valueOf(readWriteUserDetails.getTotalRequests());
        holder.show_totalRequests.setText(reqNo);
        holder.linear_new_req_time.setVisibility(View.VISIBLE);
        Date d = new Date(readWriteUserDetails.getLatestRequestTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd-MM-yy ");
        String date  = dateFormat.format(d);
        holder.show_LatestRequestTime.setText(date);

        holder.recyclerView_relative_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowAllRequestsActivity.class);
                intent.putExtra("usersUID", readWriteUserDetails.getUserId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return readRequestUserDetailsArraylist.size();
    }

    public class ShowRequestUsersViewHolder extends RecyclerView.ViewHolder {

        TextView show_name, show_totalRequests, show_LatestRequestTime;
        ImageView person_imageView;
        ProgressBar progressBar;
        ImageButton three_dot_menu;
        RelativeLayout recyclerView_relative_btn;
        LinearLayout linear_new_req_time;

        public ShowRequestUsersViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            show_name = itemView.findViewById(R.id.show_name);
            show_totalRequests = itemView.findViewById(R.id.show_totalRequests);
            show_LatestRequestTime = itemView.findViewById(R.id.show_LatestRequestTime);
            person_imageView = itemView.findViewById(R.id.person_imageView);
            three_dot_menu = itemView.findViewById(R.id.three_dot_menu);
            linear_new_req_time = itemView.findViewById(R.id.linear_new_req_time);
            recyclerView_relative_btn = itemView.findViewById(R.id.recyclerView_relative_btn);
        }
    }
}
