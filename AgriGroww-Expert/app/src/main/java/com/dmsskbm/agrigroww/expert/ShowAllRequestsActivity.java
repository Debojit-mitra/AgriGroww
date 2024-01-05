package com.dmsskbm.agrigroww.expert;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dmsskbm.agrigroww.expert.adapters.ShowRequestsAdapter;
import com.dmsskbm.agrigroww.expert.models.ModelLoadRequests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseUser firebaseUser;
    ArrayList<ModelLoadRequests> modelLoadRequestsArrayList;
    ShowRequestsAdapter showRequestsAdapter;
    String usersUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_requests);

        requests_recyclerView = findViewById(R.id.requests_recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        String expertUid = firebaseUser.getUid();
        modelLoadRequestsArrayList = new ArrayList<>();
        usersUID = getIntent().getStringExtra("usersUID");
        showRequestsAdapter = new ShowRequestsAdapter(this, modelLoadRequestsArrayList, usersUID);


        /*DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails").child(usersUID);
        referenceProfile.child("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                modelLoadRequestsArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelLoadRequests modelLoadRequests = dataSnapshot.getValue(ModelLoadRequests.class);
                    modelLoadRequestsArrayList.add(modelLoadRequests);
                }
                Collections.reverse(modelLoadRequestsArrayList);
                showRequestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails").child(usersUID).child("requests");
        referenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelLoadRequestsArrayList.clear();

                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String reqKey = dataSnapshot.getKey();
                        //Log.e("reqKey", reqKey);
                        referenceProfile.child(reqKey).child("members").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String members = snapshot.getValue(String.class);

                                    try{
                                        if (members.contains(expertUid)) {
                                            ModelLoadRequests modelLoadRequests = dataSnapshot.getValue(ModelLoadRequests.class);
                                            modelLoadRequestsArrayList.add(modelLoadRequests);
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                showRequestsAdapter.notifyDataSetChanged();
                                requests_recyclerView.scheduleLayoutAnimation();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                showRequestsAdapter.notifyDataSetChanged();
                requests_recyclerView.scheduleLayoutAnimation();
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