package com.dmsskbm.agrigroww.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;

import com.dmsskbm.agrigroww.admin.adapters.AddExpertAdapter;
import com.dmsskbm.agrigroww.admin.adapters.ShowExpertsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddExpertActivity extends AppCompatActivity {

    ArrayList<ReadWriteUserDetails> addExpertsAdapterArrayList = new ArrayList<>();
    RecyclerView experts_recyclerView;
    SwipeRefreshLayout pullToRefresh;
    AddExpertAdapter addExpertAdapter;
    String userUID, reqNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expert);

        experts_recyclerView = findViewById(R.id.experts_recyclerView);
        addExpertsAdapterArrayList = new ArrayList<>();
        pullToRefresh = findViewById(R.id.pullToRefresh);

        pullToRefresh.setRefreshing(true);

        userUID = getIntent().getStringExtra("userUID");
        reqNo = getIntent().getStringExtra("reqNo");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ExpertDetails");

        experts_recyclerView.setLayoutManager(new LinearLayoutManager(AddExpertActivity.this));
        experts_recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(AddExpertActivity.this, R.anim.item_layout_animation));
        addExpertAdapter = new AddExpertAdapter(addExpertsAdapterArrayList, AddExpertActivity.this, userUID, reqNo);
        experts_recyclerView.setAdapter(addExpertAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addExpertsAdapterArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                    addExpertsAdapterArrayList.add(readWriteUserDetails);
                }
                Collections.sort(addExpertsAdapterArrayList, new Comparator<ReadWriteUserDetails>() {
                    @Override
                    public int compare(ReadWriteUserDetails readWriteUserDetails, ReadWriteUserDetails t1) {
                        return readWriteUserDetails.getFullName().compareToIgnoreCase(t1.getFullName());
                    }
                });
                // totalExperts = "Total Experts: " + snapshot.getChildrenCount();
            // total_experts_count.setText(totalExperts);
                pullToRefresh.setRefreshing(false);
                addExpertAdapter.notifyDataSetChanged();
                experts_recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}