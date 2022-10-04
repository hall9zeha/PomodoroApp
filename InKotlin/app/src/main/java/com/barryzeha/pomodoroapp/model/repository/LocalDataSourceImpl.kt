package com.barryzeha.pomodoroapp.model.repository

import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.model.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class LocalDataSourceImpl: LocalDataSource {
    private val db=MyApp.taskDB!!.taskDao()
    private val taskList= arrayListOf<TaskModel>()
    override suspend fun saveTask(taskModel: TaskModel)= withContext(Dispatchers.IO) {
        db.saveTask(taskModel)
    }

    override suspend fun getAllTask(): List<TaskModel> = withContext(Dispatchers.IO) {
        taskList.addAll(db.getAllTask())
        return@withContext taskList
    }
}