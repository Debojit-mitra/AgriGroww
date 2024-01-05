package com.dmsskbm.agrigroww.admin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmsskbm.agrigroww.admin.R;
import com.dmsskbm.agrigroww.admin.ReadWriteUserDetails;
import com.dmsskbm.agrigroww.admin.adapters.ShowRequestUsers;
import com.dmsskbm.agrigroww.admin.models.ModelRequests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class ReqFragment extends Fragment {

    RecyclerView requestedUsers_recyclerView;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth authProfile;
    ArrayList<ReadWriteUserDetails> readRequestUserDetailsArraylist;
    ShowRequestUsers showRequestUsersAdapter;
    String AdminUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_req, container, false);
        requestedUsers_recyclerView = view.findViewById(R.id.requestedUsers_recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        authProfile = FirebaseAuth.getInstance();
        readRequestUserDetailsArraylist = new ArrayList<>();
        showRequestUsersAdapter = new ShowRequestUsers(requireContext(), readRequestUserDetailsArraylist);

        AdminUID = authProfile.getCurrentUser().getUid();

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("userDetails");
        Query query = firebaseDatabase.getReference().child("userDetails").orderByChild("requestCreated");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readRequestUserDetailsArraylist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.hasChild("requests")){
                        ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                        readRequestUserDetailsArraylist.add(readWriteUserDetails);

                                Collections.sort(readRequestUserDetailsArraylist,new Comparator<ReadWriteUserDetails>() {
                                    @Override
                                    public int compare(ReadWriteUserDetails t1, ReadWriteUserDetails t2) {
                                        try{
                                            return Long.compare(t2.getLatestRequestTime(), t1.getLatestRequestTime());
                                        } catch (Exception e){
                                            e.printStackTrace();
                                            return 0;
                                        }
                                    }
                                });
                                showRequestUsersAdapter.notifyDataSetChanged();
                                requestedUsers_recyclerView.scheduleLayoutAnimation();
                            }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        requestedUsers_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        requestedUsers_recyclerView.setAdapter(showRequestUsersAdapter);

    return view;
    }
}