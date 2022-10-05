package com.barryzeha.pomodoroapp.common

import android.widget.Button
import android.widget.ImageView
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.databinding.FragmentMainBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

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
fun ImageView.loadUrl(res:Int){
    Glide.with(context)
        .load(res)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .into(this)
}