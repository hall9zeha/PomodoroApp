package com.barryzeha.pomodoroapp.common.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.barryzeha.pomodoroapp.common.PomodoroTimer
import com.barryzeha.pomodoroapp.common.TimerState

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 10/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class MyBackgroundService: Service() {

    private val myBinder= MyLocalBinder()


    var pomodoro:PomodoroTimer?=null
    private var clientCallback:PomodoroTimer.PomodoroTimerListener?=null

   /* override fun onCreate() {
    super.onCreate()
        val startIntent = Intent(applicationContext, MyBackgroundService::class.java)
        startIntent.putExtra("inputExtra", "SERVICIO INICIADO")
        ContextCompat.startForegroundService(applicationContext, startIntent)
    }*/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


       //instanciamos la clase del timer
       pomodoro=PomodoroTimer()
       initPomodoroListener()
       pomodoro?.startTimer(0,10)

       Log.d("MY-SERVICE", "Servicio conectado")
        return START_NOT_STICKY

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

            override fun timerState(timerState: TimerState) {
                super.timerState(timerState)
                clientCallback?.timerState(timerState)
            }

            override fun cycleState(isWorkTime: Boolean) {
                super.cycleState(isWorkTime)
                clientCallback?.cycleState(isWorkTime)
            }
        }
    }
    fun registerOnPomodoroListener(callback: PomodoroTimer.PomodoroTimerListener) {
        clientCallback = callback
    }

    fun unregisterPomodoroListener(){
        clientCallback=null
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