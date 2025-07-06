package com.barryzeha.pomodoroapp.model.repository;

import androidx.lifecycle.MutableLiveData;

import com.barryzeha.pomodoroapp.model.TaskModel;

import java.util.List;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public interface LocalDataSource {
    void saveTask(TaskModel taskModel);
    void callTasks();
    MutableLiveData<List<TaskModel>> getAllTask();

    TaskModel getTask(int id);
    void deleteAll();
    void deleteTask(int id);

}
