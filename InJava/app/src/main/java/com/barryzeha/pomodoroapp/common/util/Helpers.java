package com.barryzeha.pomodoroapp.common.util;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.barryzeha.pomodoroapp.MyApp;
import com.barryzeha.pomodoroapp.R;
import com.barryzeha.pomodoroapp.view.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public  class Helpers {
    private static final  String WORK_CHANNEL_NOTIFY= MyApp.context.getString(R.string.workChannelId);
    private static final String BREAK_CHANNEL_NOTIFY= MyApp.context.getString(R.string.breakChannelId);
    private static final int NOTIFICATION_ID = 0;

    public static String convertTimeInMillisToDate(Long timeInMillis){
       SimpleDateFormat timeFormat= new SimpleDateFormat("dd-MM-yyyy", Locale.ROOT);
       return timeFormat.format(timeInMillis);
   }

   public static String convertTimeInMillisToTimeFormat(Long timeInMillis){
        Long HH= TimeUnit.MILLISECONDS.toHours(timeInMillis);
        Long MM= TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;
        Long SS= TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;
        return String.format("%02d:%02d:%02d",HH,MM,SS);
   }
    public static void loadUrl(int res, ImageView imageView){
        Glide.with(MyApp.context)
                .load(res)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView);
    }

    public static boolean checkServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager)  MyApp.context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
   public static void  createNotificationChannel(String channelName){
        final Uri workTimeSoundUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ MyApp.context.getPackageName() +"/raw/"+ "nuclear_alarm");
        final Uri breakTimeSoundUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://" + MyApp.context.getPackageName() +"/raw/"+ "alarm_clock");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            final NotificationChannel workNotificationChannel = new NotificationChannel(
                    WORK_CHANNEL_NOTIFY, channelName,NotificationManager.IMPORTANCE_LOW
            );
            final NotificationChannel breakNotificationChannel = new NotificationChannel(
                    BREAK_CHANNEL_NOTIFY, channelName, NotificationManager.IMPORTANCE_LOW
            );

            final AudioAttributes audioAttributes= new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            workNotificationChannel.enableLights(true);
            workNotificationChannel.setLightColor(Color.GREEN);
            workNotificationChannel.enableVibration(true);
            workNotificationChannel.setDescription(MyApp.context.getString(R.string.app_name));
            workNotificationChannel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            workNotificationChannel.setSound(workTimeSoundUri,audioAttributes);

            breakNotificationChannel.enableLights(true);
            breakNotificationChannel.setLightColor(Color.YELLOW);
            breakNotificationChannel.enableVibration(true);
            breakNotificationChannel.setDescription(MyApp.context.getString(R.string.app_name));
            breakNotificationChannel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            breakNotificationChannel.setSound(breakTimeSoundUri,audioAttributes);

            final NotificationManager notificationManager = MyApp.context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(workNotificationChannel);
            notificationManager.createNotificationChannel(breakNotificationChannel);
        }
   }
    public static void sendNotification(Context context, String message, boolean  isWorkTime){
        NotificationManager notificationManager = (NotificationManager) ContextCompat.getSystemService(MyApp.context, NotificationManager.class);
        if(notificationManager != null){
            notificationManager.cancelAll();

            final Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent=null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                pendingIntent=PendingIntent.getActivity(context,NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE);
            }

            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder=null;

            if(isWorkTime){
                builder= new NotificationCompat.Builder(context,context.getString(R.string.workChannelId));
            }
            else{
                builder = new NotificationCompat.Builder(context, context.getString(R.string.breakChannelId));
            }
            builder.setSmallIcon(R.drawable.lemon2)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setSound(alarm)
                    .addAction(
                            R.drawable.lemon2,
                            context.getString(R.string.notification_action),
                            pendingIntent
                    );
            notificationManager.notify(NOTIFICATION_ID, builder.build());


        }
   }
   public static void cancelAllNotifications(){
       NotificationManager notificationManager = (NotificationManager) ContextCompat.getSystemService(MyApp.context, NotificationManager.class);
       assert notificationManager != null;
       notificationManager.cancelAll();
   }
   public static Boolean checkPermission(String permission){
       return ContextCompat.checkSelfPermission(MyApp.context, permission) == PackageManager.PERMISSION_GRANTED;
   }
}
