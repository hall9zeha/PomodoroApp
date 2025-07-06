package com.barryzeha.pomodoroapp.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.barryzeha.pomodoroapp.model.TaskModel

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/
@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTask(taskModel:TaskModel)
    @Query("select * from task_table order by endTaskTimestamp desc")
    fun getAllTask():MutableList<TaskModel>
    @Query("select * from task_table where id =:id")
    fun getTask(id:Int):TaskModel
    @Query("delete  from task_table")
    fun clearHistory()
    @Query("delete  from task_table where id=:id")
    fun deleteTask(id:Int)
}