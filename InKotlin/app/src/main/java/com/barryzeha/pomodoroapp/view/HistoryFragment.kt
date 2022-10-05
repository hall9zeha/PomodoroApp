package com.barryzeha.pomodoroapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.barryzeha.pomodoroapp.common.util.Scoped
import com.barryzeha.pomodoroapp.databinding.FragmentHistoryBinding
import com.barryzeha.pomodoroapp.model.TaskModel
import com.barryzeha.pomodoroapp.model.adapters.HistoryAdapter
import com.barryzeha.pomodoroapp.viewModel.HistoryViewModel
import kotlinx.coroutines.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private val historyViewModel:HistoryViewModel by viewModels()
    private var bind:FragmentHistoryBinding?=null
    private lateinit var historyAdapter:HistoryAdapter

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

    }

    private fun setUpAdapter()=with(bind){
        this?.let{
            historyAdapter= HistoryAdapter()
            rvHistory.apply {
                setHasFixedSize(true)
                layoutManager=LinearLayoutManager(requireActivity())
                adapter=historyAdapter
            }
        }

    }

    private fun deleteTaskHistory(taskModel: TaskModel){

    }

    private fun setupViewModel() {
        CoroutineScope(Dispatchers.Main).launch {
            historyViewModel.getAllTask().observe(viewLifecycleOwner
            ) { historial ->

                historyAdapter.add(historial)
            }
        }
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