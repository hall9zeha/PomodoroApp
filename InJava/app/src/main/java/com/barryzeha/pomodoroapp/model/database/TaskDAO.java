package com.barryzeha.pomodoroapp.model.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.barryzeha.pomodoroapp.model.TaskModel;

import java.util.List;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
@Dao
public interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveTask(TaskModel taskModel);

    @Query("select *  from task_table order by endTaskTimestamp desc")
    List<TaskModel> getAllTask();

    @Query("select * from task_table where id =:idTask")
    TaskModel getTask(int idTask);

    @Query("delete from task_table")
    void clearHystoryTask();

    @Query("delete  from task_table where id =:idTask")
    void deleteTask(int idTask);
}
