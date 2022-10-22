package com.barryzeha.pomodoroapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barryzeha.pomodoroapp.R;
import com.barryzeha.pomodoroapp.databinding.FragmentTabsBinding;
import com.barryzeha.pomodoroapp.model.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class FragmentTabs extends Fragment {
    private FragmentTabsBinding bind;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity()!=null){
            bind= FragmentTabsBinding.inflate(inflater,container,false);
            return bind.getRoot();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPagerAdapter adapter= new ViewPagerAdapter(getChildFragmentManager(), getLifecycle());

        bind.viewPagerMain.setOffscreenPageLimit(3);
        bind.viewPagerMain.setAdapter(adapter);

        new TabLayoutMediator(bind.tabLayoutMain.tabLayoutMain, bind.viewPagerMain, (tab, position) -> {
            switch(position){
                case 0:
                    tab.setIcon(R.drawable.ic_timer);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_history);
                    break;
                case 2:
                    tab.setIcon(R.drawable.ic_settings);
                    break;
            }
        }).attach();
    }
}