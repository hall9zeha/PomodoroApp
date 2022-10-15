package com.barryzeha.pomodoroapp.common

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import com.barryzeha.pomodoroapp.MyApp
import java.util.*
import javax.security.auth.login.LoginException

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 11/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class PomodoroTimer {

    var pomodoroCallback:PomodoroTimerListener ?=null
    var timer: CountDownTimer?=null
    var workCyclesNum:Int= 0
    private var timeResume:Long=0L
    var workCyclesCount:Int=0
    lateinit var timerState:TimerState
    var totalTime:Long=0
    private var progressAnimator:Int=0

    var breakCyclesNum:Int=0
    private var isWorkTime=true
    var endTimestamp:Long=0



    fun startTimer(minutes:Int, seconds:Int){
        //usamos esta constante para sacar el porcentaje del progress bar
        //ya que al no variar dentro del temporizador podremos pausar y reanudar el progressbar correctamente
        val initialTimeOfCycle=((seconds * 1000) + 1000).toLong()
        //***********************************************************

        var minutesInMillis: Long=((seconds *1000) + 1000).toLong()

        when(timerState){
            TimerState.NotStarted -> minutesInMillis= ((seconds *1000 + 1000)).toLong()
            TimerState.OnPause -> {
                minutesInMillis = if(breakCyclesNum>0){
                    timeResume
                } else{
                    ((seconds *1000 + 1000)).toLong()
                }

            }
            TimerState.OnStart ->minutesInMillis= ((seconds *1000 + 1000)).toLong()
            else->{}
        }

        //Seteamos el valor del timer como iniciado
        timerState=TimerState.OnStart
        pomodoroCallback?.timerState(timerState)


        timer = object : CountDownTimer(minutesInMillis, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millis: Long) {
               //capturamos el tiempo para cuando se detenga el timer
                timeResume=millis
               //var progressValue=(millis.toInt()  * 100) / minutesInMillis.toInt()
               val progressValue=(millis.toInt()  * 100) / initialTimeOfCycle.toInt()
               //guardamos el porcentaje para actualizar constantemente el progressIndicator
               pomodoroCallback?.onTick(millis,progressValue)
         }

            override fun onFinish() {

                totalTime= (totalTime + minutesInMillis) - 1000
                timeResume -= 1000
                switchWorkAndBreakCycle()

                if(breakCyclesNum>0) {
                    isWorkTime = !isWorkTime
                    initTimer()
                    timerState= TimerState.OnStart
                    pomodoroCallback?.timerState(timerState)
                    pomodoroCallback?.onFinish(workCyclesNum)
                }
                else{
                    totalTime -= timeResume
                   endTimestamp= Calendar.getInstance().timeInMillis
                   timerState= TimerState.OnStop
                    pomodoroCallback?.timerState(timerState)

                    initValues()
                }
            }
        }

        timer?.start()

    }

    private fun switchWorkAndBreakCycle(){
        if(isWorkTime){
            workCyclesNum -= 1
        }
        else{
            breakCyclesNum -= 1
        }
    }
    private fun initTimer(){

        if(isWorkTime){
            //Seteamos es valor del enum para que el observador en el fragment sepa que mensaje mostrar
            pomodoroCallback?.timerState(TimerState.CompletedBreak)
            startTimer(0,10)
            isWorkTime=true
        }
        else {
            //Seteamos es valor del enum para que el observador en el fragment sepa que mensaje mostrar
            pomodoroCallback?.timerState(TimerState.CompletedTask)
            if(workCyclesNum == 0)startTimer(0,7) else startTimer(0,5)

            isWorkTime=false
        }

    }
    fun nextCycle(){
        if(workCyclesNum > 0) {

            switchWorkAndBreakCycle()
            timer?.cancel()
            isWorkTime = true
            breakCyclesNum -= 1
            initTimer()
        }
        else{

            stopTimer()
            // TODO:  mensaje

        }
    }

    fun stopTimer(){
        timer?.cancel()
        //guardamos los ciclos completados  porque se borrará despues de presionar stop o finalizar la tarea
        workCyclesCount = workCyclesNum
        //*******
        breakCyclesNum=0
        workCyclesNum=0
        timer?.onFinish()
        isWorkTime=true
        initValues()
    }
    fun initValues(){

        workCyclesNum= 0
        timeResume=0L
        workCyclesCount=0
        timerState= TimerState.NotStarted
        pomodoroCallback?.timerState(TimerState.NotStarted)
        pomodoroCallback?.onStop(0)
        totalTime=0
        progressAnimator=0

        breakCyclesNum=0

    }
    interface PomodoroTimerListener{
        fun onTick(millis:Long, percentProgress:Int){}
        fun onStop(valueForResetUI:Int){}
        fun onFinish(workCyclesNum:Int){}
        fun timerState(timerState:TimerState){}

    }
}