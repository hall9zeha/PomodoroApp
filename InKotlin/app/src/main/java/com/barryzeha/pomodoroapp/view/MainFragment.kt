package com.barryzeha.pomodoroapp.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.common.TimerState

import com.barryzeha.pomodoroapp.common.isServiceRunning
import com.barryzeha.pomodoroapp.common.services.MyBackgroundService
import com.barryzeha.pomodoroapp.common.util.Helpers
import com.barryzeha.pomodoroapp.databinding.FragmentMainBinding
import com.barryzeha.pomodoroapp.databinding.NewTaskBinding

import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel
import com.barryzeha.pomodoroapp.viewModel.TimerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MainFragment : Fragment(),ServiceConnection {


    private var param1: String? = null
    private var param2: String? = null

    private  var _bind: FragmentMainBinding? = null
    private val bind get() = _bind
    private val taskViewModel:HistoryViewModel by viewModels()
    private val timerViewModel:TimerViewModel by viewModels()
    private lateinit var taskModel:TaskModel
    private var initTimestamp:Long?=null
    private var workCyclesNum=0
    private var breakCyclesNum=0
    private var timeOfCycleWork=0
    private var timeOfCycleBreak=0
    private var timeOfLastBreakCycle=0
    private var taskName=""
    private var haveATask=false
    private  var serviceIntent:Intent?=null
    private var myService:MyBackgroundService?=null
    private var callBackTimer = object: TimerViewModel.PomodoroTimerListener{
        override fun onTick(millis: Long, percentProgress: Int) {
            super.onTick(millis, percentProgress)
        }

        override fun onReset() {
            super.onReset()
        }

        override fun onStart() {
            super.onStart()
        }

        override fun onStop() {
            super.onStop()
        }

        override fun onNextInterval(intervalIndex: Int) {
            super.onNextInterval(intervalIndex)
        }

        override fun onFinish(intervalIndex: Int) {
            super.onFinish(intervalIndex)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
       // setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.let{
            _bind= FragmentMainBinding.inflate(inflater,container,false)
            bind?.let {
                return it.root
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskModel= TaskModel()

        timerViewModel.initValues()

        enableStopAndNextButton(false)
        setUpListeners()
        setUpPreferencesOfPomodoro()
        observersViewModelProperties()
        Helpers.createNotificationChannel(getString(R.string.notification_channel_id),getString(R.string.channel_name))
        Log.e("SERVICE",    requireContext().isServiceRunning(MyBackgroundService::class.java).toString() )
    }



    private fun observersViewModelProperties()=with(bind) {
        this?.let {
            timerViewModel.timerState.observe(viewLifecycleOwner) {
                when (it) {
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
                tvWorkCycle.text=timerViewModel.workCyclesNum.value.toString()
            }
            timerViewModel.progressAnimator.observe(viewLifecycleOwner) {progress->
                pbTimer.progress = progress
            }
            timerViewModel.textTimerProgress.observe(viewLifecycleOwner){
                   tvMainCycle.text=Helpers.convertTimeInMillisToTimeFormat(it)
            }
        }
    }
    private fun setUpListeners()=with(bind) {
        this?.let{ bind->
            btnStart.setOnClickListener {

                when (timerViewModel.timerState.value) {
                    TimerState.NotStarted -> {
                        if (!haveATask) {
                            Toast.makeText(
                                context,
                                activity?.getString(R.string.firtAddOneTask),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            initTimestamp = Calendar.getInstance().timeInMillis
                            timerViewModel.workCyclesNum.value=MyApp.prefsDefault.getString("numCycles","4")!!.toInt()
                            timerViewModel.cyclesBreakCount.value=MyApp.prefsDefault.getString("numCycles","4")!!.toInt()
                            timerViewModel.startTimer(0, 10)
                        }
                    }
                    TimerState.OnStart -> {
                        timerViewModel.timer.cancel()
                        timerViewModel.timerState.value = TimerState.OnPause
                    }
                    TimerState.OnPause -> {
                        timerViewModel.startTimer(0, 10)
                        timerViewModel.timerState.value = TimerState.OnStart
                    }
                    TimerState.OnStop -> {
                        saveHistoryTask()
                    }
                    else -> {}
                }
            }
            btnStop.setOnClickListener {
                timerViewModel.endTimestamp.value=Calendar.getInstance().timeInMillis
                timerViewModel.stopTimer()
            }
            btnNext.setOnClickListener {
                timerViewModel.nextCycle()
            }
            fabAddTask.setOnClickListener{
                if(timerViewModel.workCyclesNum.value!! >0){
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

    private fun setUpPreferencesOfPomodoro(){

        timeOfCycleWork=MyApp.prefsDefault.getString("workTime","25")!!.toInt()
        timeOfCycleBreak=MyApp.prefsDefault.getString("breakTime","5")!!.toInt()
        workCyclesNum=MyApp.prefsDefault.getString("numCycles","4")!!.toInt()
        timeOfLastBreakCycle=MyApp.prefsDefault.getString("breakLastTime","15")!!.toInt()
        breakCyclesNum=workCyclesNum - 1

        bind?.tvWorkCycle?.text=workCyclesNum.toString()
    }
    private fun saveHistoryTask(){

            taskModel.initTaskTimestamp = initTimestamp!!
            taskModel.endTaskTimestamp = timerViewModel.endTimestamp.value!!
            taskModel.taskName = taskName
            taskModel.totalCycles = MyApp.prefsDefault.getString("numCycles","4")!!.toInt() - timerViewModel.workCyclesCount.value!!
            taskModel.totalTime = timerViewModel.totalTime.value!! - 1000
        Log.e("ERROR",MyApp.prefsDefault.getString("numCycles","4")!!.toString())
        Log.e("ERROR",timerViewModel.workCyclesCount.value!!.toString())
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as  MyBackgroundService.MyLocalBinder
        myService = binder.getService()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {

    }
    override fun onStart() {
        super.onStart()
        //Iniciamos el servicio
        Toast.makeText(context, "ON-START", Toast.LENGTH_SHORT).show()
        if(!requireContext().isServiceRunning(MyBackgroundService::class.java)) {
            requireContext().bindService(Intent(requireContext(), MyBackgroundService::class.java),
                this,
                Context.BIND_AUTO_CREATE
            )

        }
        else{
            requireContext().unbindService(this)
            myService?.stopSelf()
        }
    }

    override fun onResume() {
        super.onResume()
        //myService?.stopSelf()

        myService?.getDataFromServiceUnbinded()
        Toast.makeText(context, "ON-RESUME", Toast.LENGTH_SHORT).show()
        if(MyApp.localPrefs.timerState==TimerState.NotStarted.name){
            timerViewModel.initValues()
        }else if(MyApp.localPrefs.timerState==TimerState.OnPause.name){
            Toast.makeText(context, TimerState.OnPause.name, Toast.LENGTH_SHORT).show()
            timerViewModel.onResume()
        }

    }
    override fun onPause() {
        super.onPause()

        if(timerViewModel.timerState.value==TimerState.OnStart) {
            timerViewModel.onPause()
            myService?.getValuesOfSharedPrefs()
            myService?.initTimerBackground(0,10)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        //desconectamos el fragment del servicio y lo detenemos
        requireContext().unbindService(this)
        myService?.stopSelf()

    }
}