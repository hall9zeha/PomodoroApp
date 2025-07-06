package com.barryzeha.pomodoroapp.viewModel

import androidx.lifecycle.MutableLiveData
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
class HistoryViewModel: ScopedViewModel() {
    private val localDataSource:LocalDataSource = LocalDataSourceImpl()
    private var taskList:MutableLiveData<List<TaskModel>> = MutableLiveData()


    init {
        initScope()
    }
    fun saveTask(taskModel:TaskModel){
        launch {
            localDataSource.saveTask(taskModel)
        }
    }
    suspend fun getAllTask():MutableLiveData<List<TaskModel>>{
        return localDataSource.getAllTask()
          }

    suspend fun deleteTask(id:Int){
        launch{
            localDataSource.deleteTask(id)
        }
    }
    suspend fun deleteAllTask(){
        launch {
            localDataSource.deleteAll()
        }
    }
    override fun onCleared() {
        destroyScope()
        super.onCleared()
    }


}