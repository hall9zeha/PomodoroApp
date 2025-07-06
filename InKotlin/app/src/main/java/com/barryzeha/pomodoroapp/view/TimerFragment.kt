package com.barryzeha.pomodoroapp.view

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.Snackbar
import java.util.*


class MainFragment : Fragment(),ServiceConnection {

    private  var _bind: FragmentTimerBinding? = null
    private val bind get() = _bind!!

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
    private var workTime=true
    private lateinit var launcher:ActivityResultLauncher<String>

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
            bind?.tvCycleState?.text=""
        }
          override fun onFinish(workCyclesNum: Int) {
            super.onFinish(workCyclesNum)
            bind?.tvWorkCycle?.text=workCyclesNum.toString()
        }
           override fun timerState(timerState: TimerState?) {
            super.timerState(timerState)
            timerState?.let{updateUIButtons(timerState)}
        }

        override fun cycleState(isWorkTime: Boolean) {
            super.cycleState(isWorkTime)
            workTime=isWorkTime
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
        requestLauncher()
        enableStopAndNextButton(false)
        setUpListeners()
        Helpers.createNotificationChannel(getString(R.string.channel_name))
        Log.e("SERVICE",    requireContext().isServiceRunning(MyBackgroundService::class.java).toString() )
    }
    private fun requestLauncher(){
        launcher= registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                addNewTaskDialog()
            }else{
                Snackbar.make(
                    bind.root,
                    getString(R.string.permissionsMsg),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(R.string.setting)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, MyApp.context.packageName)
                        startActivity(settingsIntent)
                    }
                }.show()
            }
        }
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
                    bind.fabAddTask.visibility=View.VISIBLE
                }
                TimerState.CompletedTask-> {
                    //Se notifica desde el servicio
                    //Helpers.sendNotification("${getString(R.string.goRest)}  ${MyApp.prefsDefault.getInt("breakTime",15)} ${getString(R.string.minutes)} ",workTime)
                }
                TimerState.CompletedBreak->{
                    //Se notifica desde el servicio
                    //Helpers.sendNotification(getString(R.string.goWork) ,workTime)
                }
                else->{}
            }
        }

}

    private fun setUpListeners()=with(bind) {
        this?.let{ bind->
            btnStart.setOnClickListener {
                val workTimerValue=MyApp.prefsDefault.getInt("workTime",25)
                when (myService?.pomodoro?.timerState) {
                    TimerState.NotStarted -> {
                        if (!haveATask) {
                            Toast.makeText(
                                context,
                                activity?.getString(R.string.firstAddOneTask),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            initTimestamp = Calendar.getInstance().timeInMillis

                            myService?.pomodoro?.workCyclesNum=MyApp.prefsDefault.getInt("numCycles",4)!!.toInt()
                            myService?.pomodoro?.breakCyclesNum=MyApp.prefsDefault.getInt("numCycles",4)!!.toInt()

                            myService?.pomodoro?.startTimer(workTimerValue)
                            bind.fabAddTask.visibility=View.GONE
                        }
                    }
                    TimerState.OnStart -> {
                        myService?.pomodoro?.timer?.cancel()
                        myService?.pomodoro?.timerState = TimerState.OnPause
                        pomodoro?.pomodoroCallback?.timerState(TimerState.OnPause)
                    }
                    TimerState.OnPause -> {
                        myService?.pomodoro?.startTimer( workTimerValue)
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
                bind.fabAddTask.visibility=View.VISIBLE
            }
            btnNext.setOnClickListener {
                myService?.pomodoro?.nextCycle()
            }
            fabAddTask.setOnClickListener{
                if(MyApp.localPrefs.shouldShowBatteryOptimizationWarning) {
                    Helpers.batteryOptimizationIgnoredDialog(requireContext(),
                        onAccept = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.parse("package:${requireContext().packageName}")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                requireContext().startActivity(intent)

                        }, onCancel = {
                            MyApp.localPrefs.shouldShowBatteryOptimizationWarning=false
                            if (myService?.pomodoro?.workCyclesNum!! > 0) {
                                Toast.makeText(
                                    context,
                                    activity?.getString(R.string.youAlreadyTask),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Helpers.checkNotificationPermissions(Manifest.permission.POST_NOTIFICATIONS) { isGranted ->
                                        if (isGranted) addNewTaskDialog() else launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    addNewTaskDialog()
                                }
                            }
                        })

                }else {
                    if (myService?.pomodoro?.workCyclesNum!! > 0) {
                        Toast.makeText(
                            context,
                            activity?.getString(R.string.youAlreadyTask),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Helpers.checkNotificationPermissions(Manifest.permission.POST_NOTIFICATIONS) { isGranted ->
                                if (isGranted) addNewTaskDialog() else launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            addNewTaskDialog()
                        }
                    }
                }
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
    }
    private fun saveHistoryTask(){

            taskModel.initTaskTimestamp = initTimestamp!!
            taskModel.endTaskTimestamp = pomodoro?.endTimestamp!!
            taskModel.taskName = taskName
            taskModel.totalCycles = MyApp.prefsDefault.getInt("numCycles",4)!!.toInt() - pomodoro?.workCyclesCount!!
            taskModel.totalTime = pomodoro?.totalTime!! - 1000

            try {
                taskViewModel.saveTask(taskModel)
                Toast.makeText(context, getString(R.string.saved_history), Toast.LENGTH_SHORT).show()
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
                   Toast.makeText(context, getString(R.string.add_one_task), Toast.LENGTH_SHORT).show()
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
            val serviceIntent = Intent(requireContext(),MyBackgroundService::class.java)
            ContextCompat.startForegroundService(requireContext(),serviceIntent)
            requireContext().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)

        }

    }

    override fun onResume() {
        super.onResume()
        getPreferencesOfPomodoro()
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