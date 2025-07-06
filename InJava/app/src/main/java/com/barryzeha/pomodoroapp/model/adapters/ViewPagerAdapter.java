package com.barryzeha.pomodoroapp.model.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.barryzeha.pomodoroapp.view.HistoryFragment;
import com.barryzeha.pomodoroapp.view.SettingsFragment;
import com.barryzeha.pomodoroapp.view.TimerFragment;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentManager fragmentManger, Lifecycle lifecycle){
        super(fragmentManger,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch(position){
            case 1:
                fragment=new HistoryFragment();
                break;
            case 2:
                fragment= new SettingsFragment();
                break;
            default :
                fragment = new TimerFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
