package com.dmsskbm.agrigroww.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmsskbm.agrigroww.ChatActivity;
import com.dmsskbm.agrigroww.OtpActivity;
import com.dmsskbm.agrigroww.R;
import com.dmsskbm.agrigroww.RegistrationActivity;
import com.dmsskbm.agrigroww.fragments.homeFragment;
import com.dmsskbm.agrigroww.models.ModelLoadRequests;

import java.util.ArrayList;

public class RequestAdapter  extends RecyclerView.Adapter<RequestAdapter.RequestsViewHolder>{

    Context context;
    ArrayList<ModelLoadRequests> modelLoadRequestsArrayList;

    public RequestAdapter(Context context, ArrayList<ModelLoadRequests> modelLoadRequestsArrayList) {
        this.context = context;
        this.modelLoadRequestsArrayList = modelLoadRequestsArrayList;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout, parent, false);
        return new RequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position) {

        ModelLoadRequests modelLoadRequests = modelLoadRequestsArrayList.get(position);
        holder.chatView_requestNo.setText(modelLoadRequests.getGroupName());

        holder.recyclerView_relative_chatViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("groupName", modelLoadRequests.getGroupName());
                intent.putExtra("solved", modelLoadRequests.getSolved());
                context.startActivity(intent);
            }
        });
        /*String solved = modelLoadRequests.getSolved();
        if(solved.equals("yes")){
            holder.recyclerView_relative_chatViewbtn.setBackgroundColor(context.getResources().getColor(R.color.solved_color));
        }*/

    }

    @Override
    public int getItemCount() {
        return modelLoadRequestsArrayList.size();
    }

    public class RequestsViewHolder extends RecyclerView.ViewHolder {

        TextView chatView_requestNo, chatView_lastChat;
        ProgressBar progressBarBs;
        RelativeLayout recyclerView_relative_chatViewbtn;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            chatView_requestNo = itemView.findViewById(R.id.chatView_requestNo);
            chatView_lastChat = itemView.findViewById(R.id.chatView_lastChat);
            recyclerView_relative_chatViewbtn = itemView.findViewById(R.id.recyclerView_relative_chatViewbtn);
            progressBarBs = itemView.findViewById(R.id.progressBarBs);
        }
    }

}
