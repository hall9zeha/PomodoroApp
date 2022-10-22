package com.barryzeha.pomodoroapp.view

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.barryzeha.pomodoroapp.R
import com.barryzeha.pomodoroapp.databinding.AboutThisBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>("aboutThis")?.setOnPreferenceClickListener {showAboutThisDialog(); false }
    }
    private fun showAboutThisDialog() {
        val bind: AboutThisBinding = AboutThisBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setView(bind.root)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}