package com.barryzeha.pomodoroapp.common;

import android.os.CountDownTimer;

import com.barryzeha.pomodoroapp.MyApp;

import java.util.Calendar;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public class PomodoroTimer {
    public PomodoroTimerListener pomodoroCallback;
    public CountDownTimer timer;
    public int workCyclesNum = 0,workCyclesCount=0, breakCyclesNum=0, progressAnimator=0;
    public long timeResume=0, totalTime=0,endTimestamp=0;
    public TimerState timerState;
    private boolean isWorkTime=true;


    public void startTimer(int timeMinutes){
        long initialTimeOfCycle =((long) timeMinutes * 60 *1000) + 1000;
        long minutesInMillis = ((long) timeMinutes * 60 *1000) + 1000;



        switch(timerState){
            case NotStarted:
                minutesInMillis=((long) timeMinutes * 60 *1000) + 1000;
                break;
            case OnPause:
                minutesInMillis= breakCyclesNum>0 ? timeResume:((long) timeMinutes * 60 *1000) + 1000;
                break;
            case OnStart:
                minutesInMillis = ((long) timeMinutes * 60 *1000) + 1000;
                break;
            default:
                break;
        }

        timerState=TimerState.OnStart;
        pomodoroCallback.timerState(timerState);
        pomodoroCallback.cycleState(isWorkTime);

        long finalMinutesInMillis = minutesInMillis;
        timer= new CountDownTimer(finalMinutesInMillis, 1000) {
            @Override
            public void onTick(long millis) {
                timeResume= millis;
                int progressValue= (int) ((millis  * 100) / initialTimeOfCycle);
                pomodoroCallback.onTick(millis, progressValue);
            }

            @Override
            public void onFinish() {
                totalTime = ( totalTime + initialTimeOfCycle) - 1000;
                timeResume -= 1000;
                switchWorkAndBreakCycle();
                if(breakCyclesNum>0){
                    isWorkTime = !isWorkTime;
                    initTimer();
                    timerState=TimerState.OnStart;
                    pomodoroCallback.timerState(timerState);
                    pomodoroCallback.onFinish(workCyclesNum);
                }
                else{
                    totalTime -= timeResume;
                    endTimestamp = Calendar.getInstance().getTimeInMillis();
                    timerState =TimerState.OnStop;
                    pomodoroCallback.timerState(timerState);
                    initValues();
                }
            }
        };
        timer.start();
    }

    private void switchWorkAndBreakCycle(){
        if(isWorkTime){
            workCyclesNum -=1;
        }
        else{
            breakCyclesNum -=1;
        }
    }
    private void initTimer(){
        int workTime= MyApp.prefsDefault.getInt("workTime",25);
        int breakTime= MyApp.prefsDefault.getInt("breakTime",5);
        int lastBreakTime= MyApp.prefsDefault.getInt("breakLastTime",10);

        if(isWorkTime){
            startTimer(workTime);
            isWorkTime=true;
            pomodoroCallback.timerState(TimerState.CompletedBreak);
        }
        else{
            if (workCyclesNum == 0) {
                startTimer(lastBreakTime);
            } else {
                startTimer(breakTime);
            }
            isWorkTime=false;
            pomodoroCallback.timerState(TimerState.CompletedTask);
        }
    }
    public void nextCycle(){
        if(workCyclesNum >0){
            switchWorkAndBreakCycle();
            timer.cancel();
            isWorkTime=true;
            breakCyclesNum-=1;
            initTimer();
        }
        else{
            stopTimer();
        }
    }
    public void stopTimer(){
        timer.cancel();
        workCyclesCount= workCyclesNum;
        breakCyclesNum=0;
        workCyclesNum=0;
        timer.onFinish();
        isWorkTime=true;
        initValues();
    }

    public void initValues(){
        workCyclesNum=0;
        timeResume=0;
        workCyclesCount=0;
        timerState=TimerState.NotStarted;
        pomodoroCallback.timerState(TimerState.NotStarted);
        pomodoroCallback.onStop(0);
        totalTime=0;
        progressAnimator=0;
        breakCyclesNum=0;
    }
    public interface PomodoroTimerListener{
        void onTick(long millis, int percentProgress );
        void onStop(int valueForResetUI );
        void onFinish(int workCyclesNum);
        void timerState(TimerState timerState);
        void cycleState(boolean isWorkTime);
    }
}
