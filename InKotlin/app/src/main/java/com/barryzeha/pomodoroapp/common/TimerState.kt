package com.barryzeha.pomodoroapp.common

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 05/10/2022
 * Copyright (c)  All rights reserved.
 ***/

enum class TimerState {

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

    companion object {
        fun toMyEnum(myEnumString:String):TimerState{
            try{
                return valueOf(myEnumString)
            }
            catch (e:Exception){
                return OnStop
            }
        }
    }


}