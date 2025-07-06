package com.barryzeha.pomodoroapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.databinding.FragmentTabsBinding
import com.barryzeha.pomodoroapp.model.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class FragmentTabs : Fragment() {
   private var _bind:FragmentTabsBinding?=null
    private val bind get() = _bind!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.let{
            _bind=FragmentTabsBinding.inflate(inflater,container,false)
            _bind?.let{
                return it.root
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter=ViewPagerAdapter(childFragmentManager,lifecycle)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bind.viewPagerMain.currentItem != 0) {
                    isEnabled = true
                    bind.viewPagerMain.setCurrentItem(0, true)
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        bind.viewPagerMain.offscreenPageLimit=3
        bind.viewPagerMain.adapter=adapter
        TabLayoutMediator(bind.tabLayoutMain.tabLayoutMain, bind.viewPagerMain) { tab, position ->
            when(position){
                0->{tab.setIcon(R.drawable.ic_timer)}
                1->{tab.setIcon(R.drawable.ic_history)}
                2->{tab.setIcon(R.drawable.ic_settings)}
            }
       }.attach()
    }


}