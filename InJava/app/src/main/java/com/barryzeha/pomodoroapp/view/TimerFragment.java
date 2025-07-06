package com.barryzeha.pomodoroapp.view;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barryzeha.pomodoroapp.MyApp;
import com.barryzeha.pomodoroapp.R;
import com.barryzeha.pomodoroapp.common.PomodoroTimer;
import com.barryzeha.pomodoroapp.common.TimerState;
import com.barryzeha.pomodoroapp.common.services.MyBackgroundService;
import com.barryzeha.pomodoroapp.common.util.Helpers;
import com.barryzeha.pomodoroapp.databinding.FragmentTimerBinding;
import com.barryzeha.pomodoroapp.databinding.NewTaskBinding;
import com.barryzeha.pomodoroapp.model.TaskModel;
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;


public class TimerFragment extends Fragment implements ServiceConnection {

    private FragmentTimerBinding bind;
    private HistoryViewModel historyViewModel;
    private TaskModel taskModel;
    private long initTimestamp=0;
    private int workCyclesNum=0, breakCyclesNum=0, timeOfCycleWork=0,timeOfCycleBreak=0, timeOfLastBreakCycle=0;
    private String taskName="";
    private boolean haveATask=false, isBound=false, workTime=true;
    private MyBackgroundService myService;
    private PomodoroTimer pomodoro;
    private ActivityResultLauncher<String> launcher;
    private  PomodoroTimer.PomodoroTimerListener callBackTimer= new PomodoroTimer.PomodoroTimerListener() {
        @Override
        public void onTick(long millis, int percentProgress) {
            if(bind!=null){
                bind.tvMainCycle.setText(Helpers.convertTimeInMillisToTimeFormat(millis));
                bind.pbTimer.setProgress(percentProgress);
                bind.tvWorkCycle.setText(String.valueOf(pomodoro.workCyclesNum));
            }
        }

        @Override
        public void onStop(int valueForResetUI) {
            if(bind!=null){
                bind.tvWorkCycle.setText(String.valueOf(valueForResetUI));
                bind.tvMainCycle.setText(Helpers.convertTimeInMillisToTimeFormat((long) valueForResetUI));
                bind.pbTimer.setProgress(valueForResetUI);
                bind.tvCycleState.setText("");
            }
        }

        @Override
        public void onFinish(int workCyclesNum) {

            if (bind !=null) {
                bind.tvWorkCycle.setText(String.valueOf(workCyclesNum));
            }
        }

        @Override
        public void timerState(TimerState timerState) {
            if(bind !=null) {
                updateUIButtons(timerState);
            }
        }

        @Override
        public void cycleState(boolean isWorkTime) {
            workTime=isWorkTime;
            if(isWorkTime){
                bind.tvCycleState.setText(getString(R.string.working));
            }else{
                bind.tvCycleState.setText(getString(R.string.rest));
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = FragmentTimerBinding.inflate(inflater, container, false);
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskModel = new TaskModel();
        requestLauncher();
        setUpViewModel();
        enableStopAndNextButton(false);
        setUpListeners();
        Helpers.createNotificationChannel(getString(R.string.channel_name));

    }
    private void requestLauncher(){
        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted->{
            if(isGranted){ addNewTaskDialog();}
            else{
                Snackbar.make(
                        bind.getRoot(),
                        getString(R.string.permissionsMsg),
                        Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(R.string.setting), view -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Intent settingsIntent  = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, MyApp.context.getPackageName());
                        startActivity(settingsIntent);
                    }
                }).show();
            }
        });
    }

    private void setUpViewModel() {
        historyViewModel=new ViewModelProvider(this).get(HistoryViewModel.class);
    }

    private void updateUIButtons(TimerState timerState){
        switch(timerState){
            case OnStart:
                bind.btnStart.setIconResource(R.drawable.ic_pause);
                enableStopAndNextButton(true);
                break;
            case OnPause:
                bind.btnStart.setIconResource(R.drawable.ic_play);
                break;
            case OnStop:
                saveHistoryTask();
                bind.btnStart.setIconResource(R.drawable.ic_play);
                enableStopAndNextButton(false);
                Helpers.cancelAllNotifications();
                break;
            case CompletedTask:
                Helpers.sendNotification(requireContext(),
                        String.format("%s %s %s ",getString(R.string.goRest),
                                MyApp.prefsDefault.getInt("breakTime",15),
                                getString(R.string.minutes)),
                        workTime);
                break;
            case CompletedBreak:
                Helpers.sendNotification(requireContext(),getString(R.string.goWork),workTime);
                break;
        }
    }

    private void setUpListeners(){
        bind.btnStart.setOnClickListener(v->{
            int workTimeValue=MyApp.prefsDefault.getInt("workTime",25);

            switch(myService.pomodoroTimer.timerState){
                case NotStarted:
                    if(!haveATask){
                        Toast.makeText(getContext(), getString(R.string.firstAddOneTask), Toast.LENGTH_SHORT).show();
                    }else{
                        initTimestamp= Calendar.getInstance().getTimeInMillis();
                        myService.pomodoroTimer.workCyclesNum=MyApp.prefsDefault.getInt("numCycles",4);
                        myService.pomodoroTimer.breakCyclesNum=MyApp.prefsDefault.getInt("numCycles",4);
                        myService.pomodoroTimer.startTimer(workTimeValue);
                    }
                    break;
                case OnStart:
                    myService.pomodoroTimer.timer.cancel();
                    myService.pomodoroTimer.timerState=TimerState.OnPause;
                    pomodoro.pomodoroCallback.timerState(TimerState.OnPause);
                    break;
                case OnPause:
                    myService.pomodoroTimer.startTimer(workTimeValue);
                    myService.pomodoroTimer.timerState=TimerState.OnStart;
                    pomodoro.pomodoroCallback.timerState(TimerState.OnStart);
                    break;
                case OnStop:
                    saveHistoryTask();
                    break;
            }
        });
        bind.btnStop.setOnClickListener(v->{
            myService.pomodoroTimer.endTimestamp=Calendar.getInstance().getTimeInMillis();
            myService.pomodoroTimer.stopTimer();
        });
        bind.btnNext.setOnClickListener(v-> myService.pomodoroTimer.nextCycle());
        bind.fabAddTask.setOnClickListener(v->{
            if(myService.pomodoroTimer.workCyclesNum > 0){
                Toast.makeText(getContext(), getString(R.string.youAlreadyTask), Toast.LENGTH_SHORT).show();
            }else{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
                    if(Helpers.checkPermission(Manifest.permission.POST_NOTIFICATIONS))addNewTaskDialog();
                    else launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }else {
                    addNewTaskDialog();
                }
            }
        });
    }

    private void enableStopAndNextButton(boolean state){
        bind.btnStop.setEnabled(state);
        bind.btnNext.setEnabled(state);
    }
    private void getPreferencesPomodoro(){
        timeOfCycleWork = MyApp.prefsDefault.getInt("workTime",25);
        timeOfCycleBreak = MyApp.prefsDefault.getInt("breakTime",5);
        workCyclesNum = MyApp.prefsDefault.getInt("numCycles",4);
        timeOfLastBreakCycle = MyApp.prefsDefault.getInt("breakLastTime",15);

    }
    private void saveHistoryTask(){
        int totalCycles=MyApp.prefsDefault.getInt("numCycles",4) - pomodoro.workCyclesCount;
        taskModel.setId(0);
        taskModel.setInitTaskTimestamp(initTimestamp);
        taskModel.setEndTaskTimestamp(pomodoro.endTimestamp);
        taskModel.setTaskName(taskName);
        taskModel.setTotalCycles(totalCycles);
        taskModel.setTotalTime(pomodoro.totalTime);


            try {
                new Thread(()-> historyViewModel.saveTask(taskModel)).start();
               Toast.makeText(requireActivity(), "Historial guardado", Toast.LENGTH_SHORT).show();

            }
            catch(Exception e){
                e.printStackTrace();
            }

    }

    private void addNewTaskDialog(){
        NewTaskBinding binding = NewTaskBinding.inflate(getLayoutInflater());
        new MaterialAlertDialogBuilder(requireActivity())
                .setMessage(R.string.addNewTask)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.createTask, (dialog, p1)->{
                    if(binding.edtNewTask.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Escribe una tarea por favor", Toast.LENGTH_SHORT).show();
                    }else{
                        taskName= binding.edtNewTask.getText().toString();
                        haveATask=true;
                        bind.tvTaskName.setText(taskName);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MyBackgroundService.MyLocalBinder binder =(MyBackgroundService.MyLocalBinder) service;
        isBound=true;
        myService=binder.getService();
        myService.initInstance();
        myService.initPomodoroListener();
        myService.registerOnPomodoroListener(callBackTimer);
        pomodoro=myService.pomodoroTimer;
        pomodoro.initValues();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBound=false;
    }
    private void unbindSafe(){
        if(isBound){
            requireContext().unbindService(this);
            isBound=false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!Helpers.checkServiceRunning(MyBackgroundService.class)){
            requireContext().bindService(new Intent(requireContext(),MyBackgroundService.class),this, Context.BIND_AUTO_CREATE);
            if(myService !=null)myService.registerOnPomodoroListener(callBackTimer);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferencesPomodoro();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind=null;
        if(myService!=null){
            myService.stopSelf();
            myService.unregisterPomodoroListener();
        }
        unbindSafe();
    }
}