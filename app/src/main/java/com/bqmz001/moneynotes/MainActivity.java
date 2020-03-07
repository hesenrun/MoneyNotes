package com.bqmz001.moneynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.fragment.AnalysisFragment;
import com.bqmz001.moneynotes.fragment.MainFragment;
import com.bqmz001.moneynotes.fragment.SearchFragment;
import com.bqmz001.moneynotes.fragment.SettingFragment;
import com.bqmz001.moneynotes.service.BackgroundService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, BackgroundService.class));
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("记账本");
        setSupportActionBar(toolbar);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (preferences.getBoolean("isFirst", true) == true) {
            DataCenter.createNew();
            preferences.edit().putBoolean("isFirst", false).commit();
        }


        final List<Fragment> fs = new ArrayList<>();
        final List<String> titles = Arrays.asList("概览", "账单", "分析", "我");
        fs.add(new MainFragment());
        fs.add(new SearchFragment());
        fs.add(new AnalysisFragment());
        fs.add(new SettingFragment());
        ViewPager vp = findViewById(R.id.viewpager);
        vp.setOffscreenPageLimit(4);
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fs.get(position);
            }

            @Override
            public int getCount() {
                return fs.size();
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);


    }


}
