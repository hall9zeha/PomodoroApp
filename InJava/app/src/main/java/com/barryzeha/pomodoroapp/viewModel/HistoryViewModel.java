package com.barryzeha.pomodoroapp.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.barryzeha.pomodoroapp.model.TaskModel;
import com.barryzeha.pomodoroapp.model.repository.LocalDataSource;
import com.barryzeha.pomodoroapp.model.repository.LocalDataSourceImpl;

import java.util.List;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public class HistoryViewModel extends ViewModel {
    private final LocalDataSource localDataSource= new LocalDataSourceImpl();

    public void saveTask(TaskModel taskModel){
        localDataSource.saveTask(taskModel);
    }
    public  void callTask(){
        localDataSource.callTasks();
    }
    public MutableLiveData<List<TaskModel>> getAllTask(){
        return localDataSource.getAllTask();
    }

    public void deleteTask(int idTask){
        localDataSource.deleteTask(idTask);
    }

    public void deleteAll(){
        localDataSource.deleteAll();
    }
}
