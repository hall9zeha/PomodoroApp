package com.barryzeha.pomodoroapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.barryzeha.pomodoroapp.BuildConfig;
import com.barryzeha.pomodoroapp.R;
import com.barryzeha.pomodoroapp.databinding.AboutThisBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Preference aboutThisPref = findPreference("aboutThis");
        assert aboutThisPref != null;
        aboutThisPref.setSummary(BuildConfig.VERSION_NAME);
        aboutThisPref.setOnPreferenceClickListener(preference ->{
                showAboutThisDialog();
                return false;
        });
    }

    private void showAboutThisDialog(){
        AboutThisBinding bind = AboutThisBinding.inflate(getLayoutInflater());
        new MaterialAlertDialogBuilder(requireContext())
                .setView(bind.getRoot())
                .setPositiveButton(R.string.ok,null)
                .show();
    }
}