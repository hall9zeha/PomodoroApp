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
    private var _bind: ActivityMainBinding? = null
    private val bind get() = _bind!!


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PomodoroApp)
        super.onCreate(savedInstanceState)
        _bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        initMainFragment()
        //setUpToolbar()


    }

    private fun initMainFragment() {
        supportFragmentManager.beginTransaction()
            //.add(bind.frmLayoutMain.id, MainFragment())
            .add(bind.frmLayoutMain.id, FragmentTabs())
            .commit()
    }

   /* private fun setUpToolbar() {
        setSupportActionBar(bind.toolbarMain.toolbarMain)
    }
    */


    private fun setUpFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(bind.frmLayoutMain.id, fragment)
            .commit()
    }
}