package com.barryzeha.pomodoroapp.common.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.barryzeha.pomodoroapp.common.PomodoroTimer;
import com.barryzeha.pomodoroapp.common.TimerState;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public class MyBackgroundService  extends Service {
    private MyLocalBinder myBinder= new MyLocalBinder();
    public PomodoroTimer pomodoroTimer;
    private PomodoroTimer.PomodoroTimerListener clientCallBack;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void initInstance(){
        pomodoroTimer=new PomodoroTimer();
    }
    public void initPomodoroListener(){
        pomodoroTimer.pomodoroCallback = new PomodoroTimer.PomodoroTimerListener() {
            @Override
            public void onTick(long millis, int percentProgress) {
                clientCallBack.onTick(millis,percentProgress);
            }

            @Override
            public void onStop(int valueForResetUI) {
                clientCallBack.onStop(valueForResetUI);
            }

            @Override
            public void onFinish(int workCyclesNum) {
                clientCallBack.onFinish(workCyclesNum);
            }

            @Override
            public void timerState(TimerState timerState) {
                clientCallBack.timerState(timerState);
            }

            @Override
            public void cycleState(boolean isWorkTime) {
                clientCallBack.cycleState(isWorkTime);
            }
        };
    }
    public void registerOnPomodoroListener(PomodoroTimer.PomodoroTimerListener callback){
        clientCallBack=callback;
    }
    public void unregisterPomodoroListener(){
        clientCallBack=null;
    }
    public   class MyLocalBinder extends Binder {
       public  MyBackgroundService getService(){
            return MyBackgroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


}
