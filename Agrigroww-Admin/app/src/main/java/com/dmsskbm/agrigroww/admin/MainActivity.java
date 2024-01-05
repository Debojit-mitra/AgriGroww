package com.dmsskbm.agrigroww.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.dmsskbm.agrigroww.admin.extras.AdapterViewPager;
import com.dmsskbm.agrigroww.admin.fragments.ExpertsFragment;
import com.dmsskbm.agrigroww.admin.fragments.ProfileFragment;
import com.dmsskbm.agrigroww.admin.fragments.ReqFragment;
import com.dmsskbm.agrigroww.admin.fragments.UsersFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    SmoothBottomBar bottomBar;
    // public ViewPager2 mainPager;
    FragmentTransaction fragmentTransaction;
    FragmentTransaction fragmentTransaction2;
    FrameLayout main_frame_layout;
    String intentString;
    //ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    private final Fragment mReqFragment = new ReqFragment();
    private final Fragment mUserFragment = new UsersFragment();
    private final Fragment mExpertsFragment = new ExpertsFragment();
    private final Fragment mProfileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mainPager = findViewById(R.id.mainPager);
        main_frame_layout = findViewById(R.id.main_frame_layout);
        bottomBar = findViewById(R.id.bottomBar);

        intentString = getIntent().getStringExtra("frgToLoad");
        //fragment loads
        if (intentString == null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
           // Fragment reqFragment = mReqFragment;
           // Fragment profileFragment = mProfileFragment;
            fragmentTransaction2.replace(R.id.main_frame_layout, mProfileFragment);
            fragmentTransaction2.hide(mProfileFragment);
            fragmentTransaction2.commit();//loaded multiple fragments while app start to remove shutters
            fragmentTransaction.add(R.id.main_frame_layout, mReqFragment);
            fragmentTransaction.commit();
        } else if (intentString.equals("mProfileFragment")) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
          //  Fragment Fragment = mProfileFragment;
            fragmentTransaction.replace(R.id.main_frame_layout, mProfileFragment);
            fragmentTransaction.commit();
            bottomBar.setItemActiveIndex(3);
        }

        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onItemSelect(int i) {

                switch (i) {
                    case 0:
                        fragment = mReqFragment;
                        break;
                    case 1:
                        fragment = mUserFragment;
                        break;
                    case 2:
                        fragment = mExpertsFragment;
                        break;
                    case 3:
                        fragment = mProfileFragment;
                        break;
                }
                if (fragment != null) {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
                    //fragmentTransaction.add(R.id.main_frame_layout, fragment);
                    if (fragment == mReqFragment) {
                        fragmentTransaction.show(mReqFragment);
                        fragmentTransaction.hide(mUserFragment);
                        fragmentTransaction.hide(mExpertsFragment);
                        fragmentTransaction.hide(mProfileFragment);
                    } else if (fragment == mUserFragment) {
                        if (getSupportFragmentManager().getFragments().contains(mUserFragment)) {
                            fragmentTransaction.show(mUserFragment);
                            fragmentTransaction.hide(mReqFragment);
                            fragmentTransaction.hide(mExpertsFragment);
                            fragmentTransaction.hide(mProfileFragment);
                        } else {
                            fragmentTransaction.add(R.id.main_frame_layout, fragment);
                            fragmentTransaction.show(fragment);
                            fragmentTransaction.hide(mReqFragment);
                            fragmentTransaction.hide(mExpertsFragment);
                            fragmentTransaction.hide(mProfileFragment);
                        }
                    } else if (fragment == mExpertsFragment) {
                        if (getSupportFragmentManager().getFragments().contains(mExpertsFragment)) {
                            fragmentTransaction.show(mExpertsFragment);
                            fragmentTransaction.hide(mReqFragment);
                            fragmentTransaction.hide(mUserFragment);
                            fragmentTransaction.hide(mProfileFragment);
                        } else {
                            fragmentTransaction.add(R.id.main_frame_layout, fragment);
                            fragmentTransaction.show(fragment);
                            fragmentTransaction.hide(mReqFragment);
                            fragmentTransaction.hide(mUserFragment);
                            fragmentTransaction.hide(mProfileFragment);
                        }
                    } else if (fragment == mProfileFragment) {
                        if (getSupportFragmentManager().getFragments().contains(mProfileFragment)) {
                            fragmentTransaction.show(mProfileFragment);
                            fragmentTransaction.hide(mReqFragment);
                            fragmentTransaction.hide(mUserFragment);
                            fragmentTransaction.hide(mExpertsFragment);
                        } else {
                            fragmentTransaction.add(R.id.main_frame_layout, fragment);
                            fragmentTransaction.show(fragment);
                            fragmentTransaction.hide(mReqFragment);
                            fragmentTransaction.hide(mUserFragment);
                            fragmentTransaction.hide(mExpertsFragment);
                        }
                    }
                        //fragmentTransaction.replace(R.id.main_frame_layout,fragment);
                        fragmentTransaction.commit();

                    } else {
                        Log.e("Fragment", "Error in creating fragment");
                    }

                return true;
            }

        });


        /*fragmentArrayList.add(reqFragment);
        fragmentArrayList.add(userFragment);
        fragmentArrayList.add(expertsFragment);
        fragmentArrayList.add(profileFragment);
        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragmentArrayList);
        mainPager.setAdapter(adapterViewPager);
        mainPager.setOffscreenPageLimit(1);


        bottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {

            switch (i) {
                case 0:
                    if(mainPager.getCurrentItem() != 0){
                        mainPager.setCurrentItem(0);
                    }
                    break;
                case 1:
                    if(mainPager.getCurrentItem() != 1){
                        mainPager.setCurrentItem(1);
                    }
                    break;
                case 2:
                    if(mainPager.getCurrentItem() != 2){
                        mainPager.setCurrentItem(2);
                    }
                    break;
                case 3:
                    if(mainPager.getCurrentItem() != 3){
                        mainPager.setCurrentItem(3);
                    }
                    break;
            }
            return true;
        });


        mainPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomBar.setItemActiveIndex(position);
            }
        });

        //decreasing swipe sensitivity of viewpager
        try {
            final Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);

            final RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(mainPager);

            final Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);

            final int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView, touchSlop * 4);//6 is empirical value
        } catch (Exception e) {
        e.printStackTrace();
        }
         */


    }

    //will not exit or close the app when back is pressed rather it will go to background
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}