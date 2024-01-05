package com.dmsskbm.agrigroww.admin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmsskbm.agrigroww.admin.R;
import com.dmsskbm.agrigroww.admin.extras.AdapterViewPager;
import com.dmsskbm.agrigroww.admin.fragments.usersTypes.AllUsersFragment;
import com.dmsskbm.agrigroww.admin.fragments.usersTypes.BanUsersFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class UsersFragment extends Fragment {

   /* ArrayList<ReadWriteUserDetails> showUsersAdapterArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmer_view;
    showUsersAdapter showUsersAdapter;
    TextView textview_users;
    MenuItem menuItem;
    SearchView searchView;
    SwipeRefreshLayout pullToRefresh;*/

    ViewPager2 usersMainPager;
    SmoothBottomBar TopBar;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final Fragment AllUsersFragment = new AllUsersFragment();
    private final Fragment BanUsersFragment = new BanUsersFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        usersMainPager = view.findViewById(R.id.usersMainPager);
        TopBar = view.findViewById(R.id.TopBar);

        fragmentArrayList.add(AllUsersFragment);
        fragmentArrayList.add(BanUsersFragment);
        AdapterViewPager adapterViewPager = new AdapterViewPager(requireActivity(), fragmentArrayList);
        usersMainPager.setAdapter(adapterViewPager);
        usersMainPager.setOffscreenPageLimit(1);


        TopBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {

            switch (i) {
                case 0:
                    if(usersMainPager.getCurrentItem() != 0){
                        usersMainPager.setCurrentItem(0);
                    }
                    break;
                case 1:
                    if(usersMainPager.getCurrentItem() != 1){
                        usersMainPager.setCurrentItem(1);
                    }
                    break;
            }
            return true;
        });


        usersMainPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TopBar.setItemActiveIndex(position);
            }
        });

        //decreasing swipe sensitivity of viewpager
        try {
            final Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);

            final RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(usersMainPager);

            final Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);

            final int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView, touchSlop * 3);//6 is empirical value
        } catch (Exception e) {
            e.printStackTrace();
        }







        return view;
    }
}