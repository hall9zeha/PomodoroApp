package com.barryzeha.pomodoroapp.common

import android.app.ActivityManager
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/

fun ImageView.loadUrl(res:Int){
    Glide.with(context)
        .load(res)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .into(this)
}

@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }
}