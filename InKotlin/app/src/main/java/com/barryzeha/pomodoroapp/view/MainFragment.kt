package com.barryzeha.pomodoroapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.common.changueIcon
import com.barryzeha.pomodoroapp.databinding.FragmentMainBinding
import com.barryzeha.pomodoroapp.databinding.NewTaskBinding
import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.viewModel.TaskViewModel
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
    private val taskViewModel:TaskViewModel by viewModels()
    private lateinit var timer:CountDownTimer
    private var isPlay=false
    private var minutesResume=0L
    private lateinit var taskModel:TaskModel
    private var initTimestamp:Long?=null
    private var endTimestamp:Long?=null
    private var totalTime:Int=0

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
        setUpTimer(2,0)
        setUpListeners()

    }


    private fun setUpListeners()=with(bind) {
        this?.let{ bind->
            btnStart.setOnClickListener {
                initTimestamp?.let{initTimestamp=Calendar.getInstance().timeInMillis}
                if(!isPlay){
                    btnStart.changueIcon(bind,true)
                    timer.start()

                    isPlay=true
                }
                else{
                    btnStart.changueIcon(bind,false)
                    timer.cancel()
                    setUpTimer(2,0)
                    isPlay=false
                }
            }
            btnStop.setOnClickListener {
                endTimestamp?.let{endTimestamp=Calendar.getInstance().timeInMillis}
                isPlay=false
                timer.cancel()
                setUpTimer(2,0)
                timer.onFinish()
            }
            btnNext.setOnClickListener {  }
            fabAddTask.setOnClickListener{addNewTaskDialog()}
        }

    }

    private fun setUpTimer(minutes:Int,seconds:Int)= with(bind) {
        this?.let{
            val minutesInMillis: Long = if(!isPlay){
                ((minutes * 60000 + 1000)).toLong()
            }else{
                minutesResume
            }


        //val secondsInMillis = (seconds * 1000).toLong()
        timer = object : CountDownTimer(minutesInMillis, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millis: Long) {
                val formatTime = DecimalFormat("00")
                val min = (millis / 60000) % 60
                minutesResume=millis
                val sec = (millis / 1000) % 60
                Log.e("Minutes", min.toString())

                pbTimer.progress = (millis.toInt() * 100) / minutesInMillis.toInt()
                tvMainCycle.text = "${formatTime.format(min)}:${formatTime.format(sec)}"
            }

            override fun onFinish() {
                //timer.cancel()
                pbTimer.progress=0
                tvMainCycle.text = "00:00"
               btnStart.changueIcon(this@with,false)
                isPlay=false
            }
        }
        //timer.start()
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