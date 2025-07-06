package com.barryzeha.pomodoroapp.common.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.common.PomodoroTimer
import com.barryzeha.pomodoroapp.common.TimerState
import com.barryzeha.pomodoroapp.common.util.FOREGROUND_SERVICE_NOTIFICATION_ID
import com.barryzeha.pomodoroapp.common.util.Helpers
import com.barryzeha.pomodoroapp.common.util.buildNotification

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 10/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class MyBackgroundService: Service(),PomodoroTimer.PomodoroTimerListener {

    private val myBinder= MyLocalBinder()
    private var workTime:Boolean=true

    var pomodoro:PomodoroTimer?=null
    private var clientCallback:PomodoroTimer.PomodoroTimerListener?=null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       //instanciamos la clase del timer

       pomodoro=PomodoroTimer()
       initPomodoroListener()

       val notification = buildNotification(getString(R.string.lemodoro_is_active),this, null, priority = NotificationCompat.PRIORITY_LOW)
       startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID,notification)

       return START_STICKY

    }
    fun initInstance(){
        pomodoro=PomodoroTimer()
    }
    fun initPomodoroListener(){

        pomodoro?.pomodoroCallback=object: PomodoroTimer.PomodoroTimerListener{

            override fun onTick(millis: Long, percentProgress: Int) {
                super.onTick(millis, percentProgress)
                clientCallback?.onTick(millis,percentProgress)

            }
            override fun onStop(valueForResetUI:Int) {
                super.onStop(valueForResetUI)
                clientCallback?.onStop(valueForResetUI)
            }

            override fun onFinish(workCyclesNum: Int) {
                super.onFinish(workCyclesNum)
                clientCallback?.onFinish(workCyclesNum)
            }
            override fun cycleState(isWorkTime: Boolean) {
                super.cycleState(isWorkTime)
                workTime = isWorkTime
                clientCallback?.cycleState(isWorkTime)
            }
            override fun timerState(timerState: TimerState?) {
                super.timerState(timerState)
                if(timerState!=null) {
                    when (timerState) {
                        TimerState.CompletedTask -> {
                            Helpers.sendNotification(
                                "${getString(R.string.goRest)}  ${
                                    MyApp.prefsDefault.getInt(
                                        "breakTime",
                                        15
                                    )
                                } ${
                                    getString(
                                        R.string.minutes
                                    )
                                } ", workTime
                            )

                        }

                        TimerState.CompletedBreak -> {
                            Helpers.sendNotification(getString(R.string.goWork), workTime)

                        }

                        else -> {}
                    }

                    clientCallback?.timerState(timerState)
                }
            }
        }
    }

    fun registerOnPomodoroListener(callback: PomodoroTimer.PomodoroTimerListener) {
        clientCallback = callback
    }

    fun unregisterPomodoroListener(){
       // clientCallback=null
    }

    inner class MyLocalBinder : Binder() {
        fun getService(): MyBackgroundService {
            return this@MyBackgroundService
        }
    }
    override fun onBind(p0: Intent?): IBinder {
        return myBinder
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("BACKGROUND-SERVICE", "SERVICE DESTROYED")
    }

}