package com.barryzeha.pomodoroapp.view

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private  var _bind: ActivityMainBinding?=null
    private val bind get() = _bind!!



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PomodoroApp)
        super.onCreate(savedInstanceState)
        _bind= ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        initMainFragment()
        setUpToolbar()
        setUpNavigation()
        //setUpTimer(2,0)
    }

    private fun initMainFragment() {
        supportFragmentManager.beginTransaction()
            .add(bind.frmLayoutMain.id,MainFragment())
            .commit()
    }

    private fun setUpToolbar() {
        setSupportActionBar(bind.toolbarMain.toolbarMain)

    }
    private fun setUpNavigation()=with(bind){
        tabLayoutMain.tabLayoutMain.addOnTabSelectedListener( object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let{
                    when(it.position){
                        0->setUpFragments(MainFragment())
                        1->setUpFragments(HistoryFragment())
                        2->setUpFragments(SettingsFragment())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
    private fun setUpFragments(fragment:Fragment){
        supportFragmentManager.beginTransaction()
            .replace(bind.frmLayoutMain.id,fragment)
            .commit()
    }

   /* private fun setUpTimer(minutes:Int,seconds:Int) {
        val minutesInMillis= ((minutes * 60000 + 1000)).toLong()
        val secondsInMillis=(seconds * 1000).toLong()
        timer = object : CountDownTimer(minutesInMillis,1000){
            @SuppressLint("SetTextI18n")
            override fun onTick(millis: Long) {
                val formatTime=DecimalFormat("00")
                val min=(millis/60000) % 60
                val sec=(millis/1000) % 60
                Log.e("Minutes", min.toString() )
                bind.pbTimer.progress = (millis.toInt() * 100)/minutesInMillis.toInt()
                bind.tvMainCycle.text="${formatTime.format(min)}:${formatTime.format(sec)}"
            }

            override fun onFinish() {
                timer.cancel()
            }
        }
        timer.start()
    }*/
}