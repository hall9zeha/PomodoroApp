package com.barryzeha.pomodoroapp.common.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
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
    private  val WORK_CHANNEL_NOTIFY=MyApp.context.getString(R.string.workChannelId)
    private  val BREAK_CHANNEL_NOTIFY=MyApp.context.getString(R.string.breakChannelId)

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


        val   workTimeSoundUri= Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ MyApp.context.packageName +"/raw/"+ "nuclear_alarm")
        val   breakTimeSoundUri= Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://" + MyApp.context.packageName+"/raw/"+ "alarm_clock")


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val workNotificationChannel = NotificationChannel(
                WORK_CHANNEL_NOTIFY,channelName,NotificationManager.IMPORTANCE_LOW
            )
            val breakNotificationChannel=NotificationChannel(
                BREAK_CHANNEL_NOTIFY,channelName,NotificationManager.IMPORTANCE_LOW
            )

           val audioAttributes = AudioAttributes.Builder()
               .setUsage(AudioAttributes.USAGE_NOTIFICATION)
               .build()

            workNotificationChannel.enableLights(true)
            workNotificationChannel.lightColor = Color.GREEN
            workNotificationChannel.enableVibration(true)
            workNotificationChannel.description = MyApp.context.getString(R.string.app_name)
            workNotificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            workNotificationChannel.setSound(Settings.System.DEFAULT_RINGTONE_URI, audioAttributes)
            workNotificationChannel.setSound(workTimeSoundUri, audioAttributes)

            breakNotificationChannel.enableLights(true)
            breakNotificationChannel.lightColor = Color.YELLOW
            breakNotificationChannel.enableVibration(true)
            breakNotificationChannel.description = MyApp.context.getString(R.string.app_name)
            breakNotificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            breakNotificationChannel.setSound(Settings.System.DEFAULT_RINGTONE_URI, audioAttributes)
            breakNotificationChannel.setSound(breakTimeSoundUri, audioAttributes)

            val notificationManager = MyApp.context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(workNotificationChannel)
            notificationManager.createNotificationChannel(breakNotificationChannel)

        }
    }
    fun sendNotification(message:String,workTime:Boolean){
        notificationManager = ContextCompat.getSystemService(MyApp.context, NotificationManager::class.java) as NotificationManager

        notificationManager?.cancelNotifications()
        notificationManager?.sendNotification(message,MyApp.context,workTime)
    }
}