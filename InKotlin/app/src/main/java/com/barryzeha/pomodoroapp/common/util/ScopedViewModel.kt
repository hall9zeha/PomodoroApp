package com.barryzeha.pomodoroapp.common.util

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 04/10/2022
 * Copyright (c)  All rights reserved.
 ***/
abstract class ScopedViewModel:ViewModel(), Scoped by Scoped.Impl() {
    init {
        initScope()
    }

    @CallSuper
    override fun onCleared() {
        destroyScope()
        super.onCleared()
    }
}