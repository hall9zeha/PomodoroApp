package com.barryzeha.pomodoroapp.viewModel

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.common.TimerState
import com.barryzeha.pomodoroapp.common.TimerState.*
import java.util.*

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 06/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class TimerViewModel:ViewModel() {

    lateinit var timer:CountDownTimer
    private var isWorkTime=true
    private val _endTimestamp=MutableLiveData<Long>()
    val endTimestamp get()  = _endTimestamp

    private val _workCyclesCount = MutableLiveData<Int>()
    val workCyclesCount get() =_workCyclesCount

    private val _workCyclesNum=MutableLiveData<Int>()
    val workCyclesNum get() = _workCyclesNum

    private val _breakCyclesNum=MutableLiveData<Int>()
    val cyclesBreakCount get() = _breakCyclesNum

    private val _timeResume=MutableLiveData<Long>()
    val timeResume get()=_timeResume

    private val _timerState = MutableLiveData<TimerState>()
    val timerState
    get() = _timerState

    private val _totalTime = MutableLiveData<Long>()
    val totalTime get() = _totalTime

    private val _progressAnimator = MutableLiveData<Int>()
    val progressAnimator  get() = _progressAnimator
    private val _textTimerProgress=MutableLiveData<Long>()
    val textTimerProgress get() = _textTimerProgress

    private var pomodoroCallback:PomodoroTimerListener ?=null

    fun initValues(){

        _workCyclesNum.value= 0
        _timeResume.value=0L
        _workCyclesCount.value=0
        _timerState.value=NotStarted
        _totalTime.value=0
        _progressAnimator.value=0
        //_cyclesBreakCount.value=MyApp.prefsDefault.getString("numCycles","4")!!.toInt() - 1
        _breakCyclesNum.value=0
        _textTimerProgress.value=0
        //Reiniciamos la preferencia local que corresponde al estado del timer en el servicio en segundo plano
        MyApp.localPrefs.timerState=""
    }
    fun startTimer(minutes:Int, seconds:Int){

            var minutesInMillis: Long=((seconds *1000) + 1000).toLong()

            when(timerState.value){
                NotStarted-> minutesInMillis= ((seconds *1000 + 1000)).toLong()
                OnPause-> {
                    minutesInMillis = if(_breakCyclesNum.value!!>0){
                        timeResume.value!!
                    } else{
                        ((seconds *1000 + 1000)).toLong()
                    }

                }
                OnStart->minutesInMillis= ((seconds *1000 + 1000)).toLong()
                else->{}
            }

           //Seteamos el valor del timer como iniciado
            _timerState.value=OnStart


            timer = object : CountDownTimer(minutesInMillis, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millis: Long) {
                   //capturamos el tiempo para cuando se detenga el timer
                    _timeResume.value= millis
                   //guardamos el porcentaje para actualizar constantemente el progressIndicator
                    _progressAnimator.postValue((millis.toInt()  * 100) / minutesInMillis.toInt())
                    _textTimerProgress.value=millis
                    //callback


                }

                override fun onFinish() {

                    _totalTime.value= (_totalTime.value!! + minutesInMillis) - 1000
                    _timeResume.value=(_timeResume.value!!) - 1000
                    switchWorkAndBreakCycle()

                    if(/*workCyclesNum.value!! >0*/_breakCyclesNum.value!!>0) {
                        isWorkTime = !isWorkTime
                        initTimer()
                        _timerState.value=OnStart
                    }
                    else{
                        _totalTime.value= _totalTime.value!! - timeResume.value!!
                        _endTimestamp.value= Calendar.getInstance().timeInMillis
                        _timerState.value=OnStop

                        initValues()
                        // TODO: mensaje de tarea terminada

                    }
                }
            }
           timer.start()

    }
    private fun initTimer(){

        if(isWorkTime){
            //Seteamos es valor del enum para que el observador en el fragment sepa que mensaje mostrar
            _timerState.value=CompletedBreak
            startTimer(0,10)
            isWorkTime=true
        }
        else {
            //Seteamos es valor del enum para que el observador en el fragment sepa que mensaje mostrar

           _timerState.value=CompletedTask
            if(_workCyclesNum.value!! == 0)startTimer(0,7) else startTimer(0,5)

            //startTimer(0,5)
            isWorkTime=false
        }

    }

    private fun switchWorkAndBreakCycle(){
        if(isWorkTime){
            _workCyclesNum.value =  _workCyclesNum.value!! - 1
       }
        else{
            _breakCyclesNum.value = _breakCyclesNum.value!! - 1
        }
    }
    fun nextCycle(){
        if(workCyclesNum.value!! > 0) {
            resetUI()
            switchWorkAndBreakCycle()
            timer.cancel()
            isWorkTime = true
            cyclesBreakCount.value  = cyclesBreakCount.value!! - 1
            initTimer()
        }
        else{
            //workCyclesNum.value= 0
            stopTimer()
            // TODO:  mensaje
            //Toast.makeText(context, "No hay más ciclos de trabajo", Toast.LENGTH_SHORT).show()
        }
}
    private fun resetUI(){
        progressAnimator.postValue(0)
        textTimerProgress.postValue(0)
    }
    fun stopTimer(){
        timer.cancel()
        //guardamos los ciclos completados  porque se borrará despues de presionar stop o finalizar la tarea
        _workCyclesCount.value = _workCyclesNum.value
        //*******
        cyclesBreakCount.value=0
        workCyclesNum.value=0
        timer.onFinish()
        isWorkTime=true
        initValues()
    }

    private fun savePreferencesIfGoToBackground(){
        MyApp.localPrefs.resumeTimeInMillis=_timeResume.value!!
        MyApp.localPrefs.breakCyclesNum=_breakCyclesNum.value!!
        MyApp.localPrefs.workCyclesNum=_workCyclesNum.value!!
        MyApp.localPrefs.isWorkTime=isWorkTime
        MyApp.localPrefs.timerState=timerState.value!!.name
    }

    fun onResume(){
        _breakCyclesNum.value=MyApp.localPrefs.breakCyclesNum
        _workCyclesNum.value=MyApp.localPrefs.workCyclesNum
        _timerState.value= Companion.toMyEnum(MyApp.localPrefs.timerState.toString())
        isWorkTime= MyApp.localPrefs.isWorkTime
        _timeResume.value=MyApp.localPrefs.resumeTimeInMillis
        startTimer(0,10)
    }
    fun onPause(){
        timer.cancel()
        savePreferencesIfGoToBackground()
    }
    interface PomodoroTimerListener{
        fun onStart(){}
        fun onTick(millis:Long, percentProgress:Int){}
        fun onStop(){}
        fun onNextInterval(intervalIndex:Int){}
        fun onFinish(intervalIndex:Int){}
        fun onReset(){}
    }

}