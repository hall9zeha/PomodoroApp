package com.barryzeha.pomodoroapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.barryzeha.pomodoroapp.R;
import com.barryzeha.pomodoroapp.common.TimerState;
import com.barryzeha.pomodoroapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_PomodoroApp);
        super.onCreate(savedInstanceState);
        bind=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        if(savedInstanceState==null){
            initMainFragment();
        }

    }
    private void initMainFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(bind.frmLayoutMain.getId(), new FragmentTabs())
                .commit();
    }
}