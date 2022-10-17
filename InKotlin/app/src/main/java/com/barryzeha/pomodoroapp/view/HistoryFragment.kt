package com.barryzeha.pomodoroapp.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.databinding.FragmentHistoryBinding
import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.model.adapters.HistoryAdapter
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*




class HistoryFragment : Fragment() {

    private val historyViewModel:HistoryViewModel by viewModels()
    private var bind:FragmentHistoryBinding?=null
    private lateinit var historyAdapter:HistoryAdapter

    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let{
            bind = FragmentHistoryBinding.inflate(inflater,container,false)
            bind?.let{
                return it.root
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupViewModel()
        setUpAdapter()
        setUpScrollListener()
        bind?.fabDeleteAll?.setOnClickListener {showAlertDialog() }
    }

    private fun setUpAdapter()=with(bind){
        this?.let{
            historyAdapter= HistoryAdapter{taskItem->
                deleteTaskHistory(taskItem)
            }
            rvHistory.apply {
                setHasFixedSize(true)
                layoutManager=LinearLayoutManager(requireActivity())
                adapter=historyAdapter
            }
        }

    }

    private fun setUpScrollListener(){
        bind?.rvHistory?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy >0) {
                    // Scroll Down
                    if (bind?.fabDeleteAll!!.isShown) {
                        bind?.fabDeleteAll?.hide();
                    }
                }
                else if (dy <0) {
                    // Scroll Up
                    if (!bind?.fabDeleteAll!!.isShown) {
                        bind?.fabDeleteAll?.show();
                    }
                }
            }
        })
    }
    private fun deleteTaskHistory(taskModel: TaskModel){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                historyViewModel.deleteTask(taskModel.id)
                historyAdapter.remove(taskModel)

            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }
    private fun deleteAllHistory(){
        CoroutineScope(Dispatchers.IO).launch{
            try{
                historyViewModel.deleteAllTask()
                historyAdapter.removeAll()

            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun setupViewModel() {
        CoroutineScope(Dispatchers.Main).launch {
            historyViewModel.getAllTask().observe(viewLifecycleOwner
            ) { historical ->

                historyAdapter.add(historical)
            }
        }
    }
    private fun showAlertDialog(){
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage(R.string.deleteAll)
            .setPositiveButton(R.string.yes
            ) { dialog, p1 -> deleteAllHistory(); dialog.dismiss()}
            .setNegativeButton(R.string.cancel,null)
            .show()
    }


    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            historyViewModel.getAllTask()
        }

    }



}


