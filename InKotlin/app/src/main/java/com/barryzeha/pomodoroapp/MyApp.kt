package com.barryzeha.pomodoroapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.barryzeha.pomodoroapp.common.util.Preferences
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

        lateinit var prefsDefault:SharedPreferences

        private lateinit var _context:Context
        val context get() = _context
        //val prefsDefault get() = _prefsDefault

        private lateinit var _localPrefs:Preferences
        val localPrefs get() = _localPrefs

    }
    override fun onCreate() {
        super.onCreate()
        _context=this
        prefsDefault=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        _localPrefs=Preferences(this)
        _taskDB = Room.databaseBuilder(applicationContext, TaskDB::class.java, "task_table").build()

    }
}