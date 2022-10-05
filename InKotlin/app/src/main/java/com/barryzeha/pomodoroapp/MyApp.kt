package com.barryzeha.pomodoroapp

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.barryzeha.pomodoroapp.model.database.TaskDAO
import com.barryzeha.pomodoroapp.model.database.TaskDB

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 03/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class MyApp : Application() {
    companion object{
        private lateinit var _taskDB: TaskDB
        val taskDB get() = _taskDB
        private var _prefsDefault:SharedPreferences?=null
        val prefsDefault get() = _prefsDefault

    }
    override fun onCreate() {
        super.onCreate()
        _prefsDefault=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        _taskDB = Room.databaseBuilder(applicationContext, TaskDB::class.java, "task_table").build()

    }
}