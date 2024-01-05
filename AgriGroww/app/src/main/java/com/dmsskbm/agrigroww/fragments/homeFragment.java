package com.dmsskbm.agrigroww.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmsskbm.agrigroww.ChangeLanguageActivity;
import com.dmsskbm.agrigroww.ChatActivity;
import com.dmsskbm.agrigroww.R;
import com.dmsskbm.agrigroww.adapters.RequestAdapter;
import com.dmsskbm.agrigroww.models.ModelLoadRequests;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class homeFragment extends Fragment {

    FloatingActionButton add_new_requests_btn;
    RecyclerView requests_recyclerView;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth authProfile;
    ArrayList<ModelLoadRequests> modelLoadRequestsArrayList;
    RequestAdapter requestAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        add_new_requests_btn = view.findViewById(R.id.add_new_requests_btn);
        requests_recyclerView = view.findViewById(R.id.requests_recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        authProfile = FirebaseAuth.getInstance();
        modelLoadRequestsArrayList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requireContext(), modelLoadRequestsArrayList);
        String userId = authProfile.getCurrentUser().getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("userDetails");
        referenceProfile.child(userId).child("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                modelLoadRequestsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelLoadRequests modelLoadRequests = dataSnapshot.getValue(ModelLoadRequests.class);
                    modelLoadRequestsArrayList.add(modelLoadRequests);
                }
                Collections.reverse(modelLoadRequestsArrayList);

            requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //requests_recyclerView.setHasFixedSize(true);
        requests_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        requests_recyclerView.setAdapter(requestAdapter);

        add_new_requests_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences options = requireContext().getSharedPreferences("checkButtonPressed", Context.MODE_PRIVATE);
                options.edit().putString("NewChatButtonPressed", "YES").apply();

                Intent intent = new Intent(requireActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });







        return view;
    }



}