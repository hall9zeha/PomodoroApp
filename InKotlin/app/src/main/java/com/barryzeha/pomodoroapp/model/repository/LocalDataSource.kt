package com.barryzeha.pomodoroapp.model.repository

import androidx.lifecycle.MutableLiveData
import com.barryzeha.pomodoroapp.model.TaskModel

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/
interface LocalDataSource {
    suspend fun saveTask(taskModel:TaskModel)
    suspend fun getAllTask():MutableLiveData<List<TaskModel>>
    suspend fun getTask(id:Int):TaskModel
    suspend fun deleteAll()
    suspend fun deleteTask(id:Int)
}