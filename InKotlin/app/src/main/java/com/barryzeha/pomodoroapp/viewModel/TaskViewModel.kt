package com.barryzeha.pomodoroapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barryzeha.pomodoroapp.common.util.ScopedViewModel
import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.model.repository.LocalDataSource
import com.barryzeha.pomodoroapp.model.repository.LocalDataSourceImpl
import kotlinx.coroutines.launch

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class TaskViewModel: ScopedViewModel() {
    private val localDataSource:LocalDataSource = LocalDataSourceImpl()
    private val taskList:MutableLiveData<List<TaskModel>> = MutableLiveData()


    init {
        initScope()
    }
    fun saveTask(taskModel:TaskModel){
        launch {
            localDataSource.saveTask(taskModel)
        }
    }
    fun getAllTask():MutableLiveData<List<TaskModel>>{
        launch {
            taskList.postValue(localDataSource.getAllTask())
        }
        return taskList
    }

    override fun onCleared() {
        destroyScope()
        super.onCleared()
    }

}