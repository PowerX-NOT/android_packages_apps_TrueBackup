package com.android.truebackup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.android.settings.truebackup.TrueBackupBackupAppListFragment
import com.android.settings.truebackup.TrueBackupPasswordFragment
import com.android.settings.truebackup.TrueBackupRestoreAppListFragment
import com.android.settings.truebackup.TrueBackupSettingsFragment

class MainActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TrueBackupSettingsFragment())
                .commit()
        }
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        pref.fragment ?: return false
        val fragment = newSettingsSubFragment(pref) ?: return false
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
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
