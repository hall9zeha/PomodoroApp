package com.barryzeha.pomodoroapp.common

import android.widget.Button
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.databinding.FragmentMainBinding

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/


fun Button.changueIcon(bind:FragmentMainBinding, play:Boolean){

    if(play){
        bind.btnStart.setIconResource(R.drawable.ic_pause)

    }
    else{
        bind.btnStart.setIconResource(R.drawable.ic_play)

    }

}