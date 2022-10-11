package com.barryzeha.pomodoroapp.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.common.util.Scoped
import com.barryzeha.pomodoroapp.databinding.FragmentHistoryBinding
import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.model.adapters.HistoryAdapter
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private val historyViewModel:HistoryViewModel by viewModels()
    private var bind:FragmentHistoryBinding?=null
    private lateinit var historyAdapter:HistoryAdapter
    private lateinit var menuHost:MenuHost
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        //setHasOptionsMenu(true)
    }

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
        onCreateMenu()
        setupViewModel()
        setUpAdapter()
        
    }

    private fun setUpAdapter()=with(bind){
        this?.let{
            historyAdapter= HistoryAdapter(){taskItem->
                deleteTaskHistory(taskItem)
            }
            rvHistory.apply {
                setHasFixedSize(true)
                layoutManager=LinearLayoutManager(requireActivity())
                adapter=historyAdapter
            }
        }

    }
    private fun onCreateMenu(){
        menuHost=requireActivity()
        menuHost.addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_clear,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.itemClear->{
                        showAlertDialog()
                        true
                    }
                    else->{false}
                }
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)

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
            ) { historial ->

                historyAdapter.add(historial)
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

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}