package com.barryzeha.pomodoroapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
@Entity(tableName = "task_table")
public class TaskModel {
    @PrimaryKey(autoGenerate = true) int id;
    String taskName;
    long initTaskTimestamp;
    long endTaskTimestamp;
    int totalCycles;
    long totalTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getInitTaskTimestamp() {
        return initTaskTimestamp;
    }

    public void setInitTaskTimestamp(long initTaskTimestamp) {
        this.initTaskTimestamp = initTaskTimestamp;
    }

    public long getEndTaskTimestamp() {
        return endTaskTimestamp;
    }

    public void setEndTaskTimestamp(long endTaskTimestamp) {
        this.endTaskTimestamp = endTaskTimestamp;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public void setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskModel taskModel = (TaskModel) o;
        return id == taskModel.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
