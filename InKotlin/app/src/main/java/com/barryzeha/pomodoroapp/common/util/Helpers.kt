package com.barryzeha.pomodoroapp.common.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 05/10/2022
 * Copyright (c)  All rights reserved.
 ***/
object Helpers {
    var  notificationManager:NotificationManager?=null
    fun convertTimeInMillisToDate(timeInMillis:Long):String{
        val timeFormat=SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        return timeFormat.format(timeInMillis).toString()
    }
    fun convertTimeInMillisToTimeFormat(timeInMillis:Long):String{

        val HH: Long = TimeUnit.MILLISECONDS.toHours(timeInMillis)
        val MM: Long = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
        val SS: Long = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
        return  String.format("%02d:%02d:%02d", HH, MM, SS)

    }
    fun createNotificationChannel(channelId:String, channelName:String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,channelName,NotificationManager.IMPORTANCE_LOW
            )
           val audioAttributes = AudioAttributes.Builder()
               .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
               .build()

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = MyApp.context.getString(R.string.app_name)
            notificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            notificationChannel.setSound(Settings.System.DEFAULT_RINGTONE_URI, audioAttributes)

            val notificationManager = MyApp.context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    fun sendNotification(message:String){
        notificationManager = ContextCompat.getSystemService(MyApp.context, NotificationManager::class.java) as NotificationManager

        notificationManager?.cancelNotifications()
        notificationManager?.sendNotification(message,MyApp.context)
    }
}