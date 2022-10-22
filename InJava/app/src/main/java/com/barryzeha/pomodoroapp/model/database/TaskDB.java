package com.barryzeha.pomodoroapp.model.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.barryzeha.pomodoroapp.model.TaskModel;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
@Database(entities = {TaskModel.class},version=1)
public abstract class TaskDB extends RoomDatabase {
    public abstract TaskDAO taskDAO();
}
