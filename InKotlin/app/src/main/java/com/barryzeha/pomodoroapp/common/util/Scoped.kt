package com.barryzeha.pomodoroapp.common.util

import androidx.annotation.RestrictTo.Scope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/
interface Scoped:CoroutineScope {
    class Impl:Scoped{
        override lateinit var job: Job
    }
    var job:Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    fun initScope(){
        job= SupervisorJob()
    }
    fun destroyScope(){
        job.cancel()
    }

}