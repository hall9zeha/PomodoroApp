package com.barryzeha.pomodoroapp.common.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.common.TimerState
import com.barryzeha.pomodoroapp.common.util.Helpers
import kotlinx.coroutines.*
import java.sql.Time
import java.util.*

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 10/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class MyBackgroundService: Service() {
    private var timerBackground:CountDownTimer?=null
    private var timeResume:Long=0
    private var isWorkTime:Boolean=true
    private var workCyclesNum:Int=0
    private var breakCyclesNum:Int=0
    private var timeResumeInBackground:Long=0
    private lateinit var timerState:TimerState
    private var totalTime:Long=0
    private var endTimestamp:Long=0
    private lateinit var globalScope:CoroutineScope
    private val myBinder= MyLocalBinder()

   /* override fun onCreate() {

        //super.onCreate()

        val startIntent = Intent(applicationContext, MyBackgroundService::class.java)
        startIntent.putExtra("inputExtra", "aaaaa")
        ContextCompat.startForegroundService(applicationContext, startIntent)
    }*/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getValuesOfSharedPrefs()
        //globalScope= CoroutineScope(Dispatchers.IO)
       // globalScope.launch {
                 //  withContext(Dispatchers.Main){

            Toast.makeText(MyApp.context, "En segundo plano", Toast.LENGTH_SHORT).show()



          //  }
            val seconds=(timeResume / 1000).toInt()
            //initTimerBackground(0,seconds)
       // }
        // return super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY

    }
    fun getValuesOfSharedPrefs(){
        timeResume=MyApp.localPrefs.resumeTimeInMillis
        workCyclesNum=MyApp.localPrefs.workCyclesNum
        breakCyclesNum=MyApp.localPrefs.breakCyclesNum
        isWorkTime=MyApp.localPrefs.isWorkTime
        timerState=TimerState.toMyEnum(MyApp.localPrefs.timerState.toString())
    }
    fun initTimerBackground(min:Int,sec:Int){
        val minutesInMillis:Long= ((sec * 1000) + 1000).toLong()

        timerBackground = object: CountDownTimer(minutesInMillis,1000){
            override fun onTick(timeInMillis: Long) {
                timeResume=timeInMillis
            }
            override fun onFinish() {
                totalTime= (totalTime + minutesInMillis) - 1000
                timeResume -= 1000
                switchWorkAndBreakCycle()

                if(breakCyclesNum>0) {
                    isWorkTime = !isWorkTime
                    initTimer()

                }
                else{
                    totalTime -= timeResume
                    endTimestamp = Calendar.getInstance().timeInMillis
                    initValues()
                    Log.e("TERMINADO", "TERMINADO" )
                    //globalScope.launch(Dispatchers.Main) {
                        Toast.makeText(MyApp.context, "Tareas terminadas", Toast.LENGTH_SHORT)
                            .show()
                   // }
               }
            }
        }
        timerBackground!!.start()
    }
    private fun initValues(){
        timerBackground=null
        timeResume=0
        isWorkTime=true
         workCyclesNum=0
        breakCyclesNum=0
        timeResumeInBackground=0
       timerState=TimerState.NotStarted
        totalTime=0
        endTimestamp=0
    }
    private fun initTimer(){

        if(isWorkTime){
            initTimerBackground(0,10)
            isWorkTime=true
           // globalScope.launch(Dispatchers.Main) {
            Toast.makeText(MyApp.context, "a trabajar", Toast.LENGTH_SHORT).show()
            Log.d("TRABAJANDO", "A TRABAJAR")
           // }
        }
        else {
           if(workCyclesNum == 0)initTimerBackground(0,7) else initTimerBackground(0,5)
            isWorkTime=false
           // globalScope.launch(Dispatchers.Main) {
            Toast.makeText(MyApp.context, "a descansar", Toast.LENGTH_SHORT).show()
            Log.d("DESCANSO", "A DESCANSAR")
           // }
        }

    }
    private fun switchWorkAndBreakCycle(){
        if(isWorkTime){
           workCyclesNum -=  1
        }
        else{
           breakCyclesNum -= 1
        }
    }
    private fun savePreferencesIfReturnToForeground(){
        MyApp.localPrefs.resumeTimeInMillis=timeResumeInBackground
        MyApp.localPrefs.breakCyclesNum=breakCyclesNum
        MyApp.localPrefs.workCyclesNum=workCyclesNum
        MyApp.localPrefs.isWorkTime=isWorkTime
        if(breakCyclesNum==0){
            MyApp.localPrefs.timerState=TimerState.NotStarted.name
        }
        else{
            MyApp.localPrefs.timerState=TimerState.OnPause.name
        }

    }

    inner class MyLocalBinder : Binder() {
        fun getService(): MyBackgroundService {
            return this@MyBackgroundService
        }
    }
    override fun onBind(p0: Intent?): IBinder {
        return myBinder
    }
    fun getDataFromServiceUnbinded(){
        timerBackground?.cancel()
        savePreferencesIfReturnToForeground()
    }
    override fun onDestroy() {
        super.onDestroy()
        timerBackground?.cancel()
        savePreferencesIfReturnToForeground()
        Log.d("BACKGROUND-SERVICE", "SERVICE DESTROYED")
    }

}