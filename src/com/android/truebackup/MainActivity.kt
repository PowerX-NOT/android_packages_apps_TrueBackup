package com.android.truebackup

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.android.settings.core.SubSettingLauncher
import com.android.settings.truebackup.TrueBackupBackupAppListFragment
import com.android.settings.truebackup.TrueBackupPasswordFragment
import com.android.settings.truebackup.TrueBackupRestoreAppListFragment
import com.android.settings.truebackup.TrueBackupSettingsFragment
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity

class MainActivity : CollapsingToolbarBaseActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(com.android.settingslib.collapsingtoolbar.R.id.content_frame, TrueBackupSettingsFragment())
                .commit()
        }
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        pref.fragment ?: return false
        val destination = when (pref.key) {
            "true_backup_password" -> TrueBackupPasswordFragment::class.java.name
            "true_backup_backup_apps" -> TrueBackupBackupAppListFragment::class.java.name
            "true_backup_restore_apps" -> TrueBackupRestoreAppListFragment::class.java.name
            else -> return false
        }
        SubSettingLauncher(this)
            .setDestination(destination)
            .setTitleText(pref.title ?: "")
            .setArguments(pref.extras ?: Bundle())
            .launch()
        return true
    }
}
