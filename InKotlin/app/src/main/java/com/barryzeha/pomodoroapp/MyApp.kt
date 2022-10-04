package com.barryzeha.pomodoroapp

import android.app.Application
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
        private var _taskDB: TaskDB? = null
        val taskDB get() = _taskDB
    }
    override fun onCreate() {
        super.onCreate()
        _taskDB?.let {
            _taskDB = Room.databaseBuilder(this, TaskDB::class.java, "task_table").build()
        }
    }
}