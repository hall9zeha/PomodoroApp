package com.barryzeha.pomodoroapp.model.repository;

import androidx.lifecycle.MutableLiveData;

import com.barryzeha.pomodoroapp.MyApp;
import com.barryzeha.pomodoroapp.model.TaskModel;
import com.barryzeha.pomodoroapp.model.database.TaskDAO;

import java.util.List;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public class LocalDataSourceImpl implements LocalDataSource{
    private final TaskDAO db= MyApp.taskDb.taskDAO();
    private final MutableLiveData<List<TaskModel>> taskList= new MutableLiveData<>();

    @Override
    public void saveTask(TaskModel taskModel) {
        db.saveTask(taskModel);
    }

    @Override
    public void callTasks() {
        taskList.postValue(db.getAllTask());
    }

    @Override
    public MutableLiveData<List<TaskModel>> getAllTask() {
        return taskList;
    }

    @Override
    public TaskModel getTask(int id) {
        return db.getTask(id);
    }

    @Override
    public void deleteAll() {
        db.clearHystoryTask();
    }

    @Override
    public void deleteTask(int id) {
        db.deleteTask(id);
    }
}
