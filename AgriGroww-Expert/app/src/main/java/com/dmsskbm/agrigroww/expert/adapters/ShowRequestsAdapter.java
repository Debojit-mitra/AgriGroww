package com.dmsskbm.agrigroww.expert.adapters;

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
import com.dmsskbm.agrigroww.expert.ChatActivity;
import com.dmsskbm.agrigroww.expert.R;
import com.dmsskbm.agrigroww.expert.models.ModelLoadRequests;

import java.util.ArrayList;

public class ShowRequestsAdapter extends RecyclerView.Adapter<ShowRequestsAdapter.ShowRequestsViewHolder> {

    Context context;
    ArrayList<ModelLoadRequests> modelLoadRequestsArrayList;
    String usersUID;

    public ShowRequestsAdapter(Context context, ArrayList<ModelLoadRequests> modelLoadRequestsArrayList, String usersUID) {
        this.context = context;
        this.modelLoadRequestsArrayList = modelLoadRequestsArrayList;
        this.usersUID = usersUID;
    }

    @NonNull
    @Override
    public ShowRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout, parent, false);
        return new ShowRequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowRequestsViewHolder holder, int position) {

        ModelLoadRequests modelRequests = modelLoadRequestsArrayList.get(position);
        holder.chatView_requestNo.setText(modelRequests.getGroupName());
        holder.recyclerView_relative_chatViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("groupName", modelRequests.getGroupName());
                intent.putExtra("usersUID", usersUID);
                intent.putExtra("solved", modelRequests.getSolved());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelLoadRequestsArrayList.size();
    }

    public class ShowRequestsViewHolder extends RecyclerView.ViewHolder {

        TextView chatView_requestNo, chatView_lastChat;
        ProgressBar progressBarBs;
        RelativeLayout recyclerView_relative_chatViewbtn;
        public ShowRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            chatView_requestNo = itemView.findViewById(R.id.chatView_requestNo);
            chatView_lastChat = itemView.findViewById(R.id.chatView_lastChat);
            recyclerView_relative_chatViewbtn = itemView.findViewById(R.id.recyclerView_relative_chatViewbtn);
            progressBarBs = itemView.findViewById(R.id.progressBarBs);
        }
    }
}
