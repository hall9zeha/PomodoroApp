package com.barryzeha.pomodoroapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.barryzeha.pomodoroapp.model.database.TaskDB;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public class MyApp extends Application {

    public  static Context context;
    public  static TaskDB taskDb;
    public  static SharedPreferences prefsDefault;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        prefsDefault= PreferenceManager.getDefaultSharedPreferences(this);
        taskDb = Room.databaseBuilder(this,TaskDB.class,"task_table").build();
    }

}
