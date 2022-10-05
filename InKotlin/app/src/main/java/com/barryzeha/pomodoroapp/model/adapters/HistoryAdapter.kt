package com.barryzeha.pomodoroapp.model.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.barryzeha.pomodoroapp.MyApp
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.common.loadUrl
import com.barryzeha.pomodoroapp.common.util.Helpers
import com.barryzeha.pomodoroapp.databinding.ItemTaskBinding
import com.barryzeha.pomodoroapp.model.TaskModel

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 05/10/2022
 * Copyright (c)  All rights reserved.
 ***/
class HistoryAdapter(/*private val onDelete:(task:TaskModel)->Unit*/) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    private var historyTaskList:ArrayList<TaskModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_task,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(historyTaskList[position])
        holder.bind.ivDelete.setOnClickListener { /*onDelete(historyTaskList[position])*/}
    }
    fun add(historyList:List<TaskModel>){
        historyList.forEach { task->
          if(!historyTaskList.contains(task)){
                historyTaskList.add(task)
                notifyItemInserted(historyTaskList.size -1)
           }
        }
    }
    fun remove(task:TaskModel){
        if(historyTaskList.contains(task)){
            historyTaskList.remove(task)
            notifyItemRemoved(historyTaskList.indexOf(task))
        }
    }
    fun removeAll(){
        if(historyTaskList.size>0)historyTaskList.clear()
    }
    override fun getItemCount(): Int = if(historyTaskList.size>0) historyTaskList.size else 0

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val bind = ItemTaskBinding.bind(itemView)
        fun onBind(task:TaskModel)=with(bind){
            tvTaskName.text=task.taskName
            tvCreateTask.text=Helpers.convertTimeInMillisToDate(task.initTaskTimestamp)
            tvEndTask.text=Helpers.convertTimeInMillisToDate(task.endTaskTimestamp)
            tvCyclesCompleted.text=task.totalCycles.toString()
            tvFocusedTime.text=Helpers.convertTimeInMillisToTimeFormat(task.totalTime)
            ivTaskLemon.loadUrl(R.drawable.lemon2)

        }
    }

}