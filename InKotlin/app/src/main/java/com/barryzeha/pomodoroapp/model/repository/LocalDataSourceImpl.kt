package com.barryzeha.pomodoroapp.model.repository

import androidx.lifecycle.MutableLiveData
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
    private val taskList : MutableLiveData<List<TaskModel>> = MutableLiveData()
    override suspend fun saveTask(taskModel: TaskModel)= withContext(Dispatchers.IO) {
        db.saveTask(taskModel)
    }

    override suspend fun getAllTask(): MutableLiveData<List<TaskModel>> = withContext(Dispatchers.IO) {
        taskList.postValue(db.getAllTask())
        return@withContext taskList
    }

    override suspend fun getTask(id: Int): TaskModel = withContext(Dispatchers.IO){
        return@withContext db.getTask(id)
    }

    override suspend fun deleteAll()= withContext(Dispatchers.IO){
       db.clearHistory()
    }

    override suspend fun deleteTask(id: Int) = withContext(Dispatchers.IO) {
        db.deleteTask(id)
    }
}