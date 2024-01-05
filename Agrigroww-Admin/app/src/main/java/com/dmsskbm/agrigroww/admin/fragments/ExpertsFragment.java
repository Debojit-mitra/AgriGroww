package com.dmsskbm.agrigroww.admin.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.dmsskbm.agrigroww.admin.R;
import com.dmsskbm.agrigroww.admin.ReadWriteUserDetails;
import com.dmsskbm.agrigroww.admin.adapters.ShowExpertsAdapter;
import com.dmsskbm.agrigroww.admin.adapters.ShowUsersAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExpertsFragment extends Fragment {

    public ArrayList<ReadWriteUserDetails> showExpertsAdapterArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    ShowExpertsAdapter showExpertsAdapter;
    String totalExperts;
    ImageButton search_button, filter_button;
    MaterialSearchBar searchBar;
    SwipeRefreshLayout pullToRefresh;
    TextView total_experts_count;
    SharedPreferences savedSearchFilterRadioButton;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_experts, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        total_experts_count = view.findViewById(R.id.total_experts_count);
        search_button = view.findViewById(R.id.search_button);
        searchBar = view.findViewById(R.id.searchBar);
        filter_button = view.findViewById(R.id.filter_button);

        savedSearchFilterRadioButton = requireContext().getSharedPreferences("savedSearchFilterRadioButton", Context.MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference("ExpertDetails");

        getExperts();
        pullToRefresh.setRefreshing(true);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRefreshedExperts();
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchParameter = savedSearchFilterRadioButton.getString("previousSavedRadioButton", "searchByName");

                if (searchParameter.equals("searchByName")) {
                    searchBar.setHint("Search user by name");
                } else if (searchParameter.equals("searchByEmail")) {
                    searchBar.setHint("Search user by Email");
                } else if (searchParameter.equals("searchByUid")) {
                    searchBar.setHint("Search user by UserId");
                }
                search_button.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                filter_button.setVisibility(View.VISIBLE);
                searchBar.performClick();
            }
        });
        searchBar.setMaxSuggestionCount(4);

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    searchBar.setVisibility(View.GONE);
                    filter_button.setVisibility(View.GONE);
                    search_button.setVisibility(View.VISIBLE);
                    if(!searchBar.getText().isEmpty()){
                        searchBar.setText("");
                    }
                    //  getRefreshedUsers();
                }
            }
            @Override
            public void onSearchConfirmed(CharSequence text) {
            }
            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String searchParameter = savedSearchFilterRadioButton.getString("previousSavedRadioButton", "searchByName");
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                showExpertsAdapter = new ShowExpertsAdapter(showExpertsAdapterArrayList, requireContext());
                recyclerView.setAdapter(showExpertsAdapter);

                if (searchParameter.equals("searchByName")) {

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            showExpertsAdapterArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                                if(readWriteUserDetails.getFullName().toLowerCase().contains(charSequence.toString().toLowerCase().trim())){
                                    showExpertsAdapterArrayList.add(readWriteUserDetails);
                                }
                            }
                            Collections.sort(showExpertsAdapterArrayList, new Comparator<ReadWriteUserDetails>() {
                                @Override
                                public int compare(ReadWriteUserDetails readWriteUserDetails, ReadWriteUserDetails t1) {
                                    return readWriteUserDetails.getFullName().compareToIgnoreCase(t1.getFullName());
                                }
                            });
                            showExpertsAdapter.notifyDataSetChanged();
                            recyclerView.scheduleLayoutAnimation();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                } else if (searchParameter.equals("searchByEmail")) {

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            showExpertsAdapterArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                                if(readWriteUserDetails.getEmail().contains(charSequence.toString().trim())){
                                    showExpertsAdapterArrayList.add(readWriteUserDetails);
                                }
                            }
                            Collections.sort(showExpertsAdapterArrayList, new Comparator<ReadWriteUserDetails>() {
                                @Override
                                public int compare(ReadWriteUserDetails readWriteUserDetails, ReadWriteUserDetails t1) {
                                    return readWriteUserDetails.getFullName().compareToIgnoreCase(t1.getFullName());
                                }
                            });
                            showExpertsAdapter.notifyDataSetChanged();
                            recyclerView.scheduleLayoutAnimation();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                } else if (searchParameter.equals("searchByUid")) {

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            showExpertsAdapterArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                                if(readWriteUserDetails.getUserId().contains(charSequence.toString().trim())){
                                    showExpertsAdapterArrayList.add(readWriteUserDetails);
                                }
                            }
                            Collections.sort(showExpertsAdapterArrayList, new Comparator<ReadWriteUserDetails>() {
                                @Override
                                public int compare(ReadWriteUserDetails readWriteUserDetails, ReadWriteUserDetails t1) {
                                    return readWriteUserDetails.getFullName().compareToIgnoreCase(t1.getFullName());
                                }
                            });
                            showExpertsAdapter.notifyDataSetChanged();
                            recyclerView.scheduleLayoutAnimation();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder logout = new AlertDialog.Builder(requireContext(), R.style.RoundedCornersDialog);
                View filterView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null);
                RadioGroup search_filter_radio_group;
                Button ok_button = filterView.findViewById(R.id.ok_button);
                Button cancel_button = filterView.findViewById(R.id.cancel_button);
                cancel_button.setVisibility(View.VISIBLE);
                ok_button.setVisibility(View.VISIBLE);
                RadioButton searchByName, searchByUid, searchByEmail;
                search_filter_radio_group = filterView.findViewById(R.id.search_filter_radio_group);
                search_filter_radio_group.setVisibility(View.VISIBLE);
                searchByName = filterView.findViewById(R.id.searchByName);
                searchByUid = filterView.findViewById(R.id.searchByUid);
                searchByEmail = filterView.findViewById(R.id.searchByEmail);

                LottieAnimationView custom_animationView = filterView.findViewById(R.id.custom_animationView);
                custom_animationView.setVisibility(View.GONE);

                String searchParameter = savedSearchFilterRadioButton.getString("previousSavedRadioButton", "searchByName");
                if (searchParameter.equals("searchByName")) {
                    searchByName.setChecked(true);
                } else if (searchParameter.equals("searchByEmail")) {
                    searchByEmail.setChecked(true);
                } else if (searchParameter.equals("searchByUid")) {
                    searchByUid.setChecked(true);
                }

                TextView custom_textview;
                custom_textview = filterView.findViewById(R.id.custom_textview);
                String custom_text = "Select search parameter!";
                custom_textview.setText(custom_text);
                custom_textview.setVisibility(View.VISIBLE);


                logout.setView(filterView);
                AlertDialog alertDialog = logout.create();
                alertDialog.setCanceledOnTouchOutside(false);
                ok_button.setTextColor(getResources().getColor(R.color.main_color));
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int checkButton = search_filter_radio_group.getCheckedRadioButtonId();

                        if (checkButton == R.id.searchByName) {
                            savedSearchFilterRadioButton.edit().putString("previousSavedRadioButton", "searchByName").apply();
                            searchBar.setHint("Search user by name");
                        } else if (checkButton == R.id.searchByEmail) {
                            savedSearchFilterRadioButton.edit().putString("previousSavedRadioButton", "searchByEmail").apply();
                            searchBar.setHint("Search user by Email");
                        } else if (checkButton == R.id.searchByUid) {
                            savedSearchFilterRadioButton.edit().putString("previousSavedRadioButton", "searchByUid").apply();
                            searchBar.setHint("Search user by UserId");
                        }
                        alertDialog.dismiss();
                    }
                });

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


            }
        });




        return view;
    }

    private void getRefreshedExperts() {

        showExpertsAdapterArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        showExpertsAdapter = new ShowExpertsAdapter(showExpertsAdapterArrayList, requireContext());
        recyclerView.setAdapter(showExpertsAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showExpertsAdapterArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                    showExpertsAdapterArrayList.add(readWriteUserDetails);
                }
                Collections.sort(showExpertsAdapterArrayList, new Comparator<ReadWriteUserDetails>() {
                    @Override
                    public int compare(ReadWriteUserDetails readWriteUserDetails, ReadWriteUserDetails t1) {
                        return readWriteUserDetails.getFullName().compareToIgnoreCase(t1.getFullName());
                    }
                });
                totalExperts = "Total Experts: " + snapshot.getChildrenCount();
                total_experts_count.setText(totalExperts);
                pullToRefresh.setRefreshing(false);
                showExpertsAdapter.notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getExperts() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showExpertsAdapterArrayList = new ArrayList<>();
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.item_layout_animation));
                showExpertsAdapter = new ShowExpertsAdapter(showExpertsAdapterArrayList, requireContext());
                recyclerView.setAdapter(showExpertsAdapter);


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showExpertsAdapterArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                            showExpertsAdapterArrayList.add(readWriteUserDetails);
                        }
                        Collections.sort(showExpertsAdapterArrayList, new Comparator<ReadWriteUserDetails>() {
                            @Override
                            public int compare(ReadWriteUserDetails readWriteUserDetails, ReadWriteUserDetails t1) {
                                return readWriteUserDetails.getFullName().compareToIgnoreCase(t1.getFullName());
                            }
                        });
                        totalExperts = "Total Experts: " + snapshot.getChildrenCount();
                        total_experts_count.setText(totalExperts);
                        pullToRefresh.setRefreshing(false);
                        showExpertsAdapter.notifyDataSetChanged();
                        recyclerView.scheduleLayoutAnimation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, 1000);

    }
}