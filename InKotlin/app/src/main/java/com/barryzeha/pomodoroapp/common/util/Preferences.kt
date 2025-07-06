package com.barryzeha.pomodoroapp.common.util

import android.content.Context
import com.barryzeha.pomodoroapp.common.TimerState

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 09/10/2022
 * Copyright (c)  All rights reserved.
 ***/
private const val BATTERY_OPTIMIZATION_IGNORE = "batteryOptimizationIgnore"
class Preferences(context:Context) {
    private val fileName="localPreferences"
    private val localPrefs=context.getSharedPreferences(fileName,Context.MODE_PRIVATE)

    var resumeTimeInMillis:Long
        get()=localPrefs.getLong("resumeTime",0)
        set(value)=localPrefs.edit().putLong("resumeTime",value).apply()

    var timerState:String?
        get()=localPrefs.getString("timerState", TimerState.OnStop.toString())
        set(value)=localPrefs.edit().putString("timerState",value).apply()

    var isInBackground:Boolean
        get()=localPrefs.getBoolean("isInBackground",false )
        set(value)=localPrefs.edit().putBoolean("isInBackground",value).apply()

    var isWorkTime:Boolean
        get()=localPrefs.getBoolean("isWorkTime",true)
        set(value)=localPrefs.edit().putBoolean("isWorkTime",value).apply()

    var workCyclesNum:Int
        get()=localPrefs.getInt("workCycles",0)
        set(value)=localPrefs.edit().putInt("workCycles",value).apply()
    var breakCyclesNum:Int
        get()=localPrefs.getInt("breakCycles",0)
        set(value)=localPrefs.edit().putInt("breakCycles",value).apply()

    var shouldShowBatteryOptimizationWarning:Boolean
        get()=localPrefs.getBoolean(BATTERY_OPTIMIZATION_IGNORE,true)
        set(value) = localPrefs.edit().putBoolean(BATTERY_OPTIMIZATION_IGNORE,value).apply()

}