package com.barryzeha.pomodoroapp.common;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public enum TimerState {
    NotStarted,
    OnStart,
    OnPause,
    OnStop,
    OnNext,
    OnCompleted,
    OnCreatedTask,
    NoHaveTask,
    CompletedTask,
    CompletedBreak;
}
