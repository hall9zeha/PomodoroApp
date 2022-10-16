package com.barryzeha.pomodoroapp.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
class HistoryAdapter(private val onDelete:(task:TaskModel)->Unit) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    private var historyTaskList:ArrayList<TaskModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_task,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(historyTaskList[position],onDelete)

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
            //primero debemos obtener la posición del elemento importante hacerlo de esta forma
            val position  = historyTaskList.indexOf(task)
            historyTaskList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun removeAll(){

        if(historyTaskList.size>0){
            val size=historyTaskList.count()
            historyTaskList.clear()
            notifyItemRangeRemoved(0,size)
        }


    }
    override fun getItemCount(): Int = if(historyTaskList.size>0) historyTaskList.size else 0

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val bind = ItemTaskBinding.bind(itemView)

        fun onBind(task: TaskModel, onDelete: (task: TaskModel) -> Unit)=with(bind){
            tvTaskName.text=task.taskName
            tvCreateTask.text="${root.context.getString(R.string.created)} ${Helpers.convertTimeInMillisToDate(task.initTaskTimestamp)}"
            tvEndTask.text="${root.context.getString(R.string.finalized)} ${Helpers.convertTimeInMillisToDate(task.endTaskTimestamp)}"

            tvCyclesCompleted.text="${root.context.getString(R.string.completedCycles)} ${task.totalCycles}"
            tvFocusedTime.text="${root.context.getString(R.string.focusedTime)} ${Helpers.convertTimeInMillisToTimeFormat(task.totalTime)}"
            ivTaskLemon.loadUrl(R.drawable.lemon2)
            ivDelete.setOnClickListener { onDelete(task)}
        }
    }

}