package com.android.truebackup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SubSettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        if (savedInstanceState != null) return

        val fragmentName = intent.getStringExtra(EXTRA_DESTINATION) ?: return finish()
        val args = intent.getBundleExtra(EXTRA_ARGS)
        title = intent.getStringExtra(EXTRA_TITLE) ?: title

        val fragment = Fragment.instantiate(this, fragmentName, args)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        val fragmentName = pref.fragment ?: return false
        val fragment = Fragment.instantiate(this, fragmentName, pref.extras)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(fragmentName)
            .commit()
        return true
    }

    companion object {
        const val EXTRA_DESTINATION = "destination"
        const val EXTRA_TITLE = "title"
        const val EXTRA_ARGS = "args"
    }
}
