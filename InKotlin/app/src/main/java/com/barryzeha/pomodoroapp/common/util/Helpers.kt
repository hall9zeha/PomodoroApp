package com.barryzeha.pomodoroapp.common.util


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 05/10/2022
 * Copyright (c)  All rights reserved.
 ***/
object Helpers {
    private  val WORK_CHANNEL_NOTIFY_ID=MyApp.context.getString(R.string.workChannelId)
    private  val BREAK_CHANNEL_NOTIFY_ID=MyApp.context.getString(R.string.breakChannelId)
    private  val DEFAULT_CHANNEL_NOTIFY_ID=MyApp.context.getString(R.string.defaultChannelId)

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
    fun createNotificationChannel(channelName:String){


        val   workTimeSoundUri= Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ MyApp.context.packageName +"/raw/"+ "timer_terminer")
        val   breakTimeSoundUri= Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://" + MyApp.context.packageName+"/raw/"+ "microwave_timer")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val workNotificationChannel = NotificationChannel(
                    WORK_CHANNEL_NOTIFY_ID, channelName,NotificationManager.IMPORTANCE_HIGH
            )
            val breakNotificationChannel=NotificationChannel(
                BREAK_CHANNEL_NOTIFY_ID,channelName,NotificationManager.IMPORTANCE_HIGH
            )
            val defaultNotificationChannel = NotificationChannel(
                DEFAULT_CHANNEL_NOTIFY_ID,channelName,NotificationManager.IMPORTANCE_LOW
            )

           val audioAttributes = AudioAttributes.Builder()
               .setUsage(AudioAttributes.USAGE_NOTIFICATION)
               .build()

            workNotificationChannel.enableLights(true)
            workNotificationChannel.lightColor = Color.GREEN
            workNotificationChannel.enableVibration(true)
            workNotificationChannel.description = MyApp.context.getString(R.string.app_name)
            workNotificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            workNotificationChannel.setSound(workTimeSoundUri, audioAttributes)

            breakNotificationChannel.enableLights(true)
            breakNotificationChannel.lightColor = Color.YELLOW
            breakNotificationChannel.enableVibration(true)
            breakNotificationChannel.description = MyApp.context.getString(R.string.app_name)
            breakNotificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            breakNotificationChannel.setSound(breakTimeSoundUri, audioAttributes)

            val notificationManager = MyApp.context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(workNotificationChannel)
            notificationManager.createNotificationChannel(breakNotificationChannel)
            notificationManager.createNotificationChannel(defaultNotificationChannel)

        }
    }
    fun sendNotification(message:String,workTime:Boolean){
        notificationManager = ContextCompat.getSystemService(MyApp.context, NotificationManager::class.java) as NotificationManager

        notificationManager?.cancelNotifications()
        val notification = buildNotification(message, MyApp.context, workTime)
        notificationManager?.notify(NOTIFICATION_ID,notification)
    }
    fun checkNotificationPermissions(permission:String, isGranted:(Boolean)->Unit){
          if(ContextCompat.checkSelfPermission(MyApp.context,permission)==PackageManager.PERMISSION_GRANTED)isGranted(true)
          else isGranted(false)
     }

    fun isBatteryOptimizationIgnored(context: Context):Boolean{
        return if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
             powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }else{
            true
        }
    }

    fun promptUserToDisableBatteryOptimization(context:Context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
            }
    }
    fun batteryOptimizationIgnoredDialog(context:Context, onAccept:()->Unit, onCancel:()->Unit){

        val fullMessage = context.getString(R.string.optimization_battery_msg)

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.battery_optimization))
            .setMessage(fullMessage)
            .setPositiveButton(context.getString(R.string.accept)) { dialog, _ ->
                onAccept()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.notShowAgain)) { dialog, _ ->
                dialog.dismiss()
                onCancel()
            }
            .show()
    }
}