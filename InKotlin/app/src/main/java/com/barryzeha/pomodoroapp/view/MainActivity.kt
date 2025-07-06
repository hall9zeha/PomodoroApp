package com.barryzeha.pomodoroapp.view

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private var _bind: ActivityMainBinding? = null
    private val bind get() = _bind!!


    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(R.style.Theme_PomodoroApp)
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            lifecycleScope.launch {
                setKeepOnScreenCondition{false}
            }
        }
        _bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(bind.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets
        }
        //initMainFragment()
        if(savedInstanceState==null) {
            initMainFragment()
        }


    }

    private fun initMainFragment() {

        supportFragmentManager.beginTransaction()
            .add(bind.frmLayoutMain.id, FragmentTabs())
            .commit()
    }






}