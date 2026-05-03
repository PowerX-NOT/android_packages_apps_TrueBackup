package com.android.truebackup

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
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
        val fragment = newSettingsSubFragment(pref) ?: return false
        supportFragmentManager.beginTransaction()
            .replace(com.android.settingslib.collapsingtoolbar.R.id.content_frame, fragment)
            .addToBackStack(pref.fragment)
            .commit()
        return true
    }

    /**
     * Prefer keyed construction over [Fragment.instantiate]: preferences only name these classes in
     * XML strings, so shrinkers may remove them from the dex while the root screen stays.
     */
    private fun newSettingsSubFragment(pref: Preference): Fragment? {
        val args = pref.extras
        return when (pref.key) {
            "true_backup_password" -> TrueBackupPasswordFragment().apply { arguments = args }
            "true_backup_backup_apps" -> TrueBackupBackupAppListFragment().apply { arguments = args }
            "true_backup_restore_apps" -> TrueBackupRestoreAppListFragment().apply { arguments = args }
            else -> null
        }
    }
}
