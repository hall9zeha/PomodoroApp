package com.barryzeha.pomodoroapp.common.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.view.MainActivity

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 09/10/2022
 * Copyright (c)  All rights reserved.
 ***/

const val FOREGROUND_SERVICE_NOTIFICATION_ID=1
const val NOTIFICATION_ID =2

fun buildNotification(message: String,
                      context: Context,
                      workTime: Boolean?,
                      priority:Int=NotificationCompat.PRIORITY_HIGH):Notification{
    val mainIntent = Intent(context,MainActivity::class.java)
    mainIntent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP

    val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        PendingIntent.getActivity(context, NOTIFICATION_ID, mainIntent,PendingIntent.FLAG_IMMUTABLE)

    }else{
        TODO("VERSION.SDK_INT < M")
    }

    val alarm: Uri =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder = NotificationCompat.Builder(
        context,
        when(workTime){
            true-> context.getString(R.string.workChannelId)
            false->context.getString(R.string.breakChannelId)
            else-> context.getString(R.string.defaultChannelId)
        }
    )
    builder.setSmallIcon(R.drawable.lemon2)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setOnlyAlertOnce(true)
        .setPriority(priority)
        .setAutoCancel(true)
        .setSound(alarm)
        .addAction(
            R.drawable.lemon2,
            context.getString(R.string.notification_action),
            pendingIntent
        )

    return builder.build()
}
fun NotificationManager.cancelNotifications(){
    cancelAll()
}