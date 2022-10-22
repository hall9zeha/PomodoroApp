package com.barryzeha.pomodoroapp.model.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzeha.pomodoroapp.R;
import com.barryzeha.pomodoroapp.common.util.Helpers;
import com.barryzeha.pomodoroapp.databinding.ItemTaskBinding;
import com.barryzeha.pomodoroapp.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

/****
 * Project PomodoroApp
 * Created by Barry Zea H. on 18/10/2022
 * Copyright (c)  All rights reserved.
 ***/
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final ListenerInterface listener;
    private  ArrayList<TaskModel> historyTaskList= new ArrayList<>();

    public HistoryAdapter(ListenerInterface listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(historyTaskList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return historyTaskList.size() > 0? historyTaskList.size() : 0;
    }


    public void add(List<TaskModel> historyList){
       for(TaskModel task: historyList){
            if(!historyTaskList.contains(task)){
                historyTaskList.add(task);
                notifyItemInserted(historyTaskList.size() - 1);
            }
       }
    }
    public void remove(TaskModel task){
        if(historyTaskList.contains(task)){
            int position = historyTaskList.indexOf(task);
            historyTaskList.remove(task);
            notifyItemRemoved(position);

        }
    }
    public void removeAll(){
        if(historyTaskList.size()>0){
            int size= historyTaskList.size();
            historyTaskList.clear();
            notifyItemRangeRemoved(0,size);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTaskBinding bind;
        private Context ctx;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bind = ItemTaskBinding.bind(itemView);
            ctx=bind.getRoot().getContext();
        }
        public void onBind(TaskModel task, ListenerInterface listener){
            bind.tvTaskName.setText(task.getTaskName());
            bind.tvCreateTask.setText(String.format("%s %s"
                    ,ctx.getString(R.string.created)
                    ,Helpers.convertTimeInMillisToDate(task.getInitTaskTimestamp())));
            bind.tvEndTask.setText(String.format("%s %s"
                    ,ctx.getString(R.string.finalized)
                    ,Helpers.convertTimeInMillisToDate(task.getEndTaskTimestamp())));
            bind.tvCyclesCompleted.setText(String.format("%s %s"
                    ,ctx.getString(R.string.completedCycles)
                    ,task.getTotalCycles()));
            bind.tvFocusedTime.setText(String.format("%s %s"
                    ,ctx.getString(R.string.focusedTime)
                    ,Helpers.convertTimeInMillisToTimeFormat(task.getTotalTime())));
            Helpers.loadUrl(R.drawable.lemon2,bind.ivTaskLemon);
            bind.ivDelete.setOnClickListener(v-> listener.onDelete(task) );
            Log.e("Millis", Helpers.convertTimeInMillisToTimeFormat(task.getTotalTime()) + "->" + task.getTotalTime());
        }
    }
    public interface ListenerInterface {
        void onDelete(TaskModel task);
    }

}
