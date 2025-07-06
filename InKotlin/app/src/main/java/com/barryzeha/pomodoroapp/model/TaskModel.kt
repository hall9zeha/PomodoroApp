package com.barryzeha.pomodoroapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/
@Entity(tableName = "task_table")
data class TaskModel(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    var taskName:String="",
    var initTaskTimestamp:Long=0L,
    var endTaskTimestamp:Long=0L,
    var totalCycles:Int=0,
    var totalTime:Long=0L
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskModel

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
