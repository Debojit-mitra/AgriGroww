package com.dmsskbm.agrigroww.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.dmsskbm.agrigroww.admin.adapters.ShowRequestsAdapter;
import com.dmsskbm.agrigroww.admin.models.ModelRequests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ShowAllRequestsActivity extends AppCompatActivity {

    RecyclerView requests_recyclerView;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth authProfile;
    ArrayList<ModelRequests> modelLoadRequestsArrayList;
    ShowRequestsAdapter showRequestsAdapter;
    String usersUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_requests);

        requests_recyclerView = findViewById(R.id.requests_recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        authProfile = FirebaseAuth.getInstance();
        modelLoadRequestsArrayList = new ArrayList<>();
        usersUID = getIntent().getStringExtra("usersUID");
        showRequestsAdapter = new ShowRequestsAdapter(this, modelLoadRequestsArrayList, usersUID);


        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails").child(usersUID);
        referenceProfile.child("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                modelLoadRequestsArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelRequests modelRequests = dataSnapshot.getValue(ModelRequests.class);
                    modelLoadRequestsArrayList.add(modelRequests);
                }
                Collections.reverse(modelLoadRequestsArrayList);
                showRequestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //requests_recyclerView.setHasFixedSize(true);
        requests_recyclerView.setLayoutManager(new LinearLayoutManager(ShowAllRequestsActivity.this));
        requests_recyclerView.setAdapter(showRequestsAdapter);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}