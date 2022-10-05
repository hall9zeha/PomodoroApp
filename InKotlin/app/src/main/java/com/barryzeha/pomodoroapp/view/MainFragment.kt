package com.barryzeha.pomodoroapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.common.Cycles
import com.barryzeha.pomodoroapp.common.changueIcon
import com.barryzeha.pomodoroapp.databinding.FragmentMainBinding
import com.barryzeha.pomodoroapp.databinding.NewTaskBinding
import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MainFragment : Fragment() {


    private var param1: String? = null
    private var param2: String? = null

    private  var _bind:FragmentMainBinding ? = null
    private val bind get() = _bind
    private val taskViewModel:HistoryViewModel by viewModels()
    private lateinit var timer:CountDownTimer
    private var isPlay=false
    private var minutesResume=0L
    private lateinit var taskModel:TaskModel
    private var initTimestamp:Long?=null
    private var endTimestamp:Long?=null
    private var totalTime:Long=0
    private var workCyclesNum=0
    private var breakCyclesNum=0
    private var timeOfCycleWork=0
    private var timeOfCycleBreak=0
    private var timeOfLastBreakCycle=0
    private var isWorkTime=true
    private var isBreakTime=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        setUpTimer(0,10)
        setUpListeners()
        setUpPreferencesOfPomodoro()

    }


    private fun setUpListeners()=with(bind) {
        this?.let{ bind->
            btnStart.setOnClickListener {

                initTimestamp=Calendar.getInstance().timeInMillis
                if(!isPlay){
                    btnStart.changueIcon(bind,true)
                    timer.start()
                    isPlay=true
                    if(workCyclesNum==0){
                        setUpPreferencesOfPomodoro()
                    }
                    else if(workCyclesNum==1){
                        workCyclesNum +=1
                        setUpPreferencesOfPomodoro()
                    }
                }
                else{
                    btnStart.changueIcon(bind,false)
                    timer.cancel()
                    setUpTimer(0,10)

                    isPlay=false
                }
            }
            btnStop.setOnClickListener {
                endTimestamp=Calendar.getInstance().timeInMillis
                stopPomodoro()
                saveHistoryTask()

            }
            btnNext.setOnClickListener {
                nextCycle()
            }
            fabAddTask.setOnClickListener{addNewTaskDialog()}
        }

    }
    private fun stopPomodoro(){
        timer.cancel()
        workCyclesNum=0
        breakCyclesNum=0
        timer.onFinish()
        setUpTimer(0,10)
        isWorkTime=true
        isPlay=false
        setUpPreferencesOfPomodoro()
    }
    private fun nextCycle(){
        if(workCyclesNum>1) {
            resetUI()
            timer.cancel()
            isWorkTime = true
            isPlay=false
            breakCyclesNum -=1
            initLemodoro()
        }
        else{
            stopPomodoro()
            Toast.makeText(context, "No hay más ciclos de trabajo", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setUpTimer(minutes:Int,seconds:Int){

        bind?.let{bind->
            val minutesInMillis: Long
            if(!isPlay){
                //minutesInMillis= ((minutes * 60000 + 1000)).toLong()
                minutesInMillis= ((seconds *1000 + 1000)).toLong()


            }else{
                minutesInMillis = if(workCyclesNum>0){
                    minutesResume
                } else{
                    ((seconds *1000 + 1000)).toLong()
                }
            }
            totalTime +=minutesInMillis

        //val secondsInMillis = (seconds * 1000).toLong()
        timer = object : CountDownTimer(minutesInMillis, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millis: Long) {
                val formatTime = DecimalFormat("00")
                val min = (millis / 60000) % 60
                minutesResume=millis

                val sec = (millis / 1000) % 60
                Log.e("Minutes", min.toString())

                bind.pbTimer.progress = (millis.toInt() * 100) / minutesInMillis.toInt()
                bind.tvMainCycle.text = "${formatTime.format(min)}:${formatTime.format(sec)}"
            }

            override fun onFinish() {
               

                resetUI()
               if(workCyclesNum >1) {
                    isWorkTime = !isWorkTime
                    initLemodoro()
                    bind.btnStart.changueIcon(bind,true)
                    isPlay=true

                  }
                else{
                   totalTime -= minutesInMillis
                    Toast.makeText(context, "Tarea terminada", Toast.LENGTH_SHORT).show()
                    bind.tvBreakCycle.text="0"
                    bind.tvWorkCycle.text="0"
                }
            }
        }
        //timer.start()
        }
    }
    private fun resetUI()=with(bind){
        this?.let {
            pbTimer.progress = 0
            tvMainCycle.text = "00:00"
            btnStart.changueIcon(this, false)
            isPlay = false

        }
    }
    private fun initLemodoro()=with(bind){

           if(isWorkTime){
                Toast.makeText(context, "Tarea iniciada", Toast.LENGTH_SHORT).show()
                setUpTimer(0,10)

                timer.start()
                isWorkTime=true
               workCyclesNum -=1
               this?.tvWorkCycle?.text=workCyclesNum.toString()
            }
            else {

                Toast.makeText(context, "Descanso iniciado", Toast.LENGTH_SHORT).show()
                setUpTimer(0,5)

                timer.start()
                isWorkTime=false
               breakCyclesNum -=1
               this?.tvBreakCycle?.text=breakCyclesNum.toString()

            }

    }
    private fun setUpPreferencesOfPomodoro(){
        timeOfCycleWork=Cycles.TIME_WORK_CYCLE.value
        timeOfCycleBreak=Cycles.TIME_BREAK_CYCLE.value
        workCyclesNum=Cycles.NUM_OF_WORK_CYCLES.value
        timeOfLastBreakCycle=Cycles.TIME_OF_LAST_CYCLE.value
        breakCyclesNum=Cycles.NUM_OF_BREAK_CYCLES.value

        bind?.tvBreakCycle?.text=breakCyclesNum.toString()
        bind?.tvWorkCycle?.text=workCyclesNum.toString()
    }
    private fun saveHistoryTask(){
        taskModel.initTaskTimestamp=initTimestamp!!
        taskModel.endTaskTimestamp=endTimestamp!!
        taskModel.taskName="prueba"
        taskModel.totalCycles=Cycles.NUM_OF_WORK_CYCLES.value - workCyclesNum
        taskModel.totalTime=totalTime
        try{
        taskViewModel.saveTask(taskModel)
            Toast.makeText(context, "Historial guardado", Toast.LENGTH_SHORT).show()
        }
        catch(e:Exception){
            e.printStackTrace()
        }
    }
    private fun addNewTaskDialog(){
        val bindDialog=NewTaskBinding.inflate(layoutInflater)
      MaterialAlertDialogBuilder(requireActivity())
           .setMessage(R.string.addNewTask)
           .setView(bindDialog.root)
           .setPositiveButton(R.string.createTask
           ) { dialog, p1 -> taskModel.taskName=bindDialog.edtNewTask.text.toString() }
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
}