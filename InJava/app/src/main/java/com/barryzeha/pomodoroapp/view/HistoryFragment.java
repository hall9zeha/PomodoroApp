package com.barryzeha.pomodoroapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzeha.pomodoroapp.R;
import com.barryzeha.pomodoroapp.databinding.FragmentHistoryBinding;
import com.barryzeha.pomodoroapp.model.TaskModel;
import com.barryzeha.pomodoroapp.model.adapters.HistoryAdapter;
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class HistoryFragment extends Fragment  implements HistoryAdapter.ListenerInterface{

    private FragmentHistoryBinding bind;
    private HistoryViewModel historyViewModel;
    private HistoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind= FragmentHistoryBinding.inflate(inflater,container,false);
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
        setUpAdapter();
        setUpScrollListener();
        bind.fabDeleteAll.setOnClickListener(v-> showDeleteDialog());
    }

    private void setUpViewModel(){
        historyViewModel= new ViewModelProvider(this).get(HistoryViewModel.class);
        new Thread(()-> historyViewModel.callTask()).start();

        historyViewModel.getAllTask().observe(getViewLifecycleOwner(), taskModels -> adapter.add(taskModels));
    }
    private void setUpAdapter(){
        adapter= new HistoryAdapter(this);
        bind.rvHistory.setHasFixedSize(true);
        bind.rvHistory.setLayoutManager(new LinearLayoutManager(requireActivity()));
        bind.rvHistory.setAdapter(adapter);

    }
    private void setUpScrollListener(){
        bind.rvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    if(bind.fabDeleteAll.isShown()){
                        bind.fabDeleteAll.hide();
                    }
                }
                else if(dy < 0){
                    if(!bind.fabDeleteAll.isShown()){
                        bind.fabDeleteAll.show();
                    }
                }
            }
        });
    }
    @Override
    public void onDelete(TaskModel task) {
        new Thread(()->{
            try{
                historyViewModel.deleteTask(task.getId());
                adapter.remove(task);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }
    private void deleteAllHistory(){
        new Thread(()->{
            try{
                historyViewModel.deleteAll();
                adapter.removeAll();
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }
    private void showDeleteDialog(){
        new MaterialAlertDialogBuilder(requireActivity())
                .setMessage(R.string.deleteAll)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    deleteAllHistory();
                    dialogInterface.dismiss();
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(()-> historyViewModel.callTask()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind=null;
    }
}