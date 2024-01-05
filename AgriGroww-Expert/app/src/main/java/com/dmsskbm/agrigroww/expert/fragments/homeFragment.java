package com.dmsskbm.agrigroww.expert.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmsskbm.agrigroww.expert.R;
import com.dmsskbm.agrigroww.expert.ReadWriteUserDetails;
import com.dmsskbm.agrigroww.expert.adapters.ShowRequestUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class homeFragment extends Fragment {

    RecyclerView requests_recyclerView;
    FirebaseDatabase firebaseDatabase;
    ArrayList<ReadWriteUserDetails> readRequestUserDetailsArraylist;
    ShowRequestUsers showRequestUsers;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        requests_recyclerView = view.findViewById(R.id.requests_recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        readRequestUserDetailsArraylist = new ArrayList<>();

        String expertUid = firebaseUser.getUid();

        showRequestUsers = new ShowRequestUsers(requireContext(), readRequestUserDetailsArraylist);

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");

        try {
        referenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readRequestUserDetailsArraylist.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userIdKey = dataSnapshot.getKey();
                    referenceProfile.child(userIdKey).child("requests").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            readRequestUserDetailsArraylist.clear();

                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                String reqKey = dataSnapshot1.getKey();

                                //   Log.e("reqKey", reqKey);

                                referenceProfile.child(userIdKey).child("requests").child(reqKey).child("members").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String members = snapshot.getValue(String.class);

                                        try{
                                            if (members.contains(expertUid)) {
                                                ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                                                if(!readRequestUserDetailsArraylist.contains(readWriteUserDetails.getUserId())){
                                                    readRequestUserDetailsArraylist.add(readWriteUserDetails);
                                                }
                                            }
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        showRequestUsers.notifyDataSetChanged();
                                        requests_recyclerView.scheduleLayoutAnimation();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                showRequestUsers.notifyDataSetChanged();
                                requests_recyclerView.scheduleLayoutAnimation();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                showRequestUsers.notifyDataSetChanged();
                requests_recyclerView.scheduleLayoutAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

            //requests_recyclerView.setHasFixedSize(true);
            requests_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            requests_recyclerView.setAdapter(showRequestUsers);


        } catch (Exception e){
            e.printStackTrace();
        }




        return view;
    }



}