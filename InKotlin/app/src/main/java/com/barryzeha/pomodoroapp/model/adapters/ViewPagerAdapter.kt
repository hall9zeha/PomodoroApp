package com.barryzeha.pomodoroapp.model.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.barryzeha.pomodoroapp.view.HistoryFragment
import com.barryzeha.pomodoroapp.view.MainFragment
import com.barryzeha.pomodoroapp.view.SettingsFragment

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 16/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class ViewPagerAdapter(fragmentManager:FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 ->{MainFragment()}
            1 ->{HistoryFragment()}
            2 ->{SettingsFragment()}
            else ->{MainFragment()}
        }
    }
}