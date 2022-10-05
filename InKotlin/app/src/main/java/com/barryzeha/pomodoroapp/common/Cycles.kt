package com.barryzeha.pomodoroapp.common

import com.barryzeha.pomodoroapp.MyApp

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 05/10/2022
 * Copyright (c)  All rights reserved.
 ***/

enum class Cycles(val value:Int) {
    TIME_WORK_CYCLE(MyApp.prefsDefault?.getString("workTime","25")!!.toInt()),
    TIME_BREAK_CYCLE(MyApp.prefsDefault?.getString("breakTime","5")!!.toInt()),
    NUM_OF_WORK_CYCLES(MyApp.prefsDefault?.getString("numCycles","4")!!.toInt()),
    NUM_OF_BREAK_CYCLES(NUM_OF_WORK_CYCLES.value - 1),
    TIME_OF_LAST_CYCLE(MyApp.prefsDefault?.getString("breakLastTime","15")!!.toInt())
}