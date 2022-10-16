package com.barryzeha.pomodoroapp.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.common.PomodoroTimer
import com.barryzeha.pomodoroapp.common.TimerState
import com.barryzeha.pomodoroapp.common.isServiceRunning
import com.barryzeha.pomodoroapp.common.services.MyBackgroundService
import com.barryzeha.pomodoroapp.common.util.Helpers
import com.barryzeha.pomodoroapp.databinding.FragmentTimerBinding
import com.barryzeha.pomodoroapp.databinding.NewTaskBinding
import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class MainFragment : Fragment(),ServiceConnection {

    private  var _bind: FragmentTimerBinding? = null
    private val bind get() = _bind

    private val taskViewModel:HistoryViewModel by viewModels()

    private lateinit var taskModel:TaskModel
    private var initTimestamp:Long?=null
    private var workCyclesNum=0
    private var breakCyclesNum=0
    private var timeOfCycleWork=0
    private var timeOfCycleBreak=0
    private var timeOfLastBreakCycle=0
    private var taskName=""
    private var haveATask=false
    private var isBound=false
    private var myService:MyBackgroundService?=null
    private var pomodoro:PomodoroTimer?=null


    private var callBackTimer=object :PomodoroTimer.PomodoroTimerListener {
        override fun onTick(millis: Long, percentProgress: Int) {
            super.onTick(millis, percentProgress)
            bind?.tvMainCycle?.text=Helpers.convertTimeInMillisToTimeFormat(millis)
            bind?.pbTimer?.progress= percentProgress
            bind?.tvWorkCycle?.text= pomodoro?.workCyclesNum.toString()
        }
        override fun onStop(valueForResetUI:Int) {
            super.onStop(valueForResetUI)
            bind?.tvWorkCycle?.text= valueForResetUI.toString()
            bind?.tvMainCycle?.text=Helpers.convertTimeInMillisToTimeFormat(valueForResetUI.toLong())
            bind?.pbTimer?.progress=valueForResetUI
        }
          override fun onFinish(workCyclesNum: Int) {
            super.onFinish(workCyclesNum)
            bind?.tvWorkCycle?.text=workCyclesNum.toString()
        }
           override fun timerState(timerState: TimerState) {
            super.timerState(timerState)
            updateUIButtons(timerState)
        }

        override fun cycleState(isWorkTime: Boolean) {
            super.cycleState(isWorkTime)
            if(isWorkTime){
                bind?.tvCycleState?.text=getString(R.string.working)
            }
            else{
                bind?.tvCycleState?.text=getString(R.string.rest)
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            _bind= FragmentTimerBinding.inflate(inflater,container,false)
            return bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskModel= TaskModel()

        enableStopAndNextButton(false)
        setUpListeners()
        Helpers.createNotificationChannel(getString(R.string.notification_channel_id),getString(R.string.channel_name))
        Log.e("SERVICE",    requireContext().isServiceRunning(MyBackgroundService::class.java).toString() )
    }


  private fun updateUIButtons(timerState: TimerState)= with(bind){
        this?.let{
            when (timerState) {
                TimerState.OnStart -> {
                    btnStart.setIconResource(R.drawable.ic_pause)
                    enableStopAndNextButton(true)
                }
                TimerState.OnPause->btnStart.setIconResource(R.drawable.ic_play)
                TimerState.OnStop->{
                    saveHistoryTask()
                    btnStart.setIconResource(R.drawable.ic_play)
                    enableStopAndNextButton(false)
                    Helpers.notificationManager?.cancelAll()
                }
                TimerState.CompletedTask-> {
                    Toast.makeText(context, "A descansar", Toast.LENGTH_SHORT).show()
                    //Helpers.sendNotification(getString(R.string.goRest))
                }

                TimerState.CompletedBreak->{
                    Toast.makeText(context, "A trabajar", Toast.LENGTH_SHORT).show()
                    //Helpers.sendNotification(getString(R.string.goWork))
                }
                else->{}
            }
        }

}

    private fun setUpListeners()=with(bind) {
        this?.let{ bind->
            btnStart.setOnClickListener {
                when (myService?.pomodoro?.timerState) {
                    TimerState.NotStarted -> {
                        if (!haveATask) {
                            Toast.makeText(
                                context,
                                activity?.getString(R.string.firtAddOneTask),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            initTimestamp = Calendar.getInstance().timeInMillis

                            myService?.pomodoro?.workCyclesNum=MyApp.prefsDefault.getInt("numCycles",4)!!.toInt()
                            myService?.pomodoro?.breakCyclesNum=MyApp.prefsDefault.getInt("numCycles",4)!!.toInt()

                            myService?.pomodoro?.startTimer(0, 10)
                        }
                    }
                    TimerState.OnStart -> {
                        myService?.pomodoro?.timer?.cancel()
                        myService?.pomodoro?.timerState = TimerState.OnPause
                        pomodoro?.pomodoroCallback?.timerState(TimerState.OnPause)
                    }
                    TimerState.OnPause -> {
                        myService?.pomodoro?.startTimer(0, 10)
                        myService?.pomodoro?.timerState = TimerState.OnStart
                        pomodoro?.pomodoroCallback?.timerState(TimerState.OnStart)
                    }
                    TimerState.OnStop -> {
                        saveHistoryTask()
                    }
                    else -> {}
                }
            }
            btnStop.setOnClickListener {
                myService?.pomodoro?.endTimestamp=Calendar.getInstance().timeInMillis
                myService?.pomodoro?.stopTimer()
            }
            btnNext.setOnClickListener {
                myService?.pomodoro?.nextCycle()
            }
            fabAddTask.setOnClickListener{

                if(myService?.pomodoro?.workCyclesNum!! > 0){
                    Toast.makeText(context, activity?.getString(R.string.youAlreagyTask), Toast.LENGTH_SHORT).show()
                }else{
                addNewTaskDialog()}


            }
        }
    }

    private fun enableStopAndNextButton(state:Boolean)= with(bind){
        this?.let{
            btnStop.isEnabled=state
            btnNext.isEnabled=state
        }
    }

    private fun getPreferencesOfPomodoro(){

        timeOfCycleWork=MyApp.prefsDefault.getInt("workTime",25)!!.toInt()
        timeOfCycleBreak=MyApp.prefsDefault.getInt("breakTime",5)!!.toInt()
        workCyclesNum=MyApp.prefsDefault.getInt("numCycles",4)!!.toInt()
        timeOfLastBreakCycle=MyApp.prefsDefault.getInt("breakLastTime",15)!!.toInt()
        breakCyclesNum=workCyclesNum - 1


    }
    private fun saveHistoryTask(){

            taskModel.initTaskTimestamp = initTimestamp!!
            taskModel.endTaskTimestamp = pomodoro?.endTimestamp!!
            taskModel.taskName = taskName
            taskModel.totalCycles = MyApp.prefsDefault.getInt("numCycles",4)!!.toInt() - pomodoro?.workCyclesCount!!
            taskModel.totalTime = pomodoro?.totalTime!! - 1000

            try {
                taskViewModel.saveTask(taskModel)
                Toast.makeText(context, "Historial guardado", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }
    private fun addNewTaskDialog(){
        val bindDialog= NewTaskBinding.inflate(layoutInflater)
      MaterialAlertDialogBuilder(requireActivity())
           .setMessage(R.string.addNewTask)
           .setView(bindDialog.root)
           .setPositiveButton(R.string.createTask
           ) { dialog, p1 ->
               if(bindDialog.edtNewTask.text.toString().isEmpty()){
                   Toast.makeText(context, "Escribe una tarea porfavor", Toast.LENGTH_SHORT).show()
               }
               else {
                   taskName = bindDialog.edtNewTask.text.toString()
                   haveATask=true
                   bind?.tvTaskName?.text=taskName
                   dialog.dismiss()
               }
           }
           .setNegativeButton(R.string.cancel,null)
           .show()
    }



    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as  MyBackgroundService.MyLocalBinder
        isBound=true
        myService = binder.getService()
        myService?.initInstance()
        myService?.initPomodoroListener()
        myService?.registerOnPomodoroListener(callBackTimer)
        pomodoro = myService?.pomodoro
        pomodoro?.initValues()

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        isBound=false

    }
    private fun unbindSafe(){
        if(isBound){
            requireContext().unbindService(this)
            isBound=false
        }
    }
    override fun onStart() {
        super.onStart()
        //Iniciamos el servicio

        if(!requireContext().isServiceRunning(MyBackgroundService::class.java)) {
            requireContext().bindService(Intent(requireContext(), MyBackgroundService::class.java),
                this,
                Context.BIND_AUTO_CREATE
            )
            myService?.registerOnPomodoroListener(callBackTimer)
        }

    }

    override fun onResume() {
        super.onResume()
        getPreferencesOfPomodoro()
    }
    override fun onStop() {
        super.onStop()
        //Si detenemos el servicio aquí no habra funcionamiento en segundo plano de las notificaciones
      /*  myService?.unregisterPomodoroListener()
        unbindSafe()*/
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _bind=null
        myService?.stopSelf()
        myService?.unregisterPomodoroListener()
        //desconectamos el fragment del servicio y lo detenemos
        unbindSafe()


    }
}