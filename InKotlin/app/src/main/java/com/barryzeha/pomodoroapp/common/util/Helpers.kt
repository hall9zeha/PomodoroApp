package com.barryzeha.pomodoroapp.common.util

import java.text.SimpleDateFormat
import java.util.*

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 05/10/2022
 * Copyright (c)  All rights reserved.
 ***/
object Helpers {

    fun convertTimeInMillisToDate(timeInMillis:Long):String{
        val timeFormat=SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT)
        return timeFormat.format(timeInMillis).toString()
    }
    fun convertTimeInMillisToTimeFormat(timeInMillis:Long):String{
        val timeFormat=SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        return timeFormat.format(timeInMillis).toString()
    }
}