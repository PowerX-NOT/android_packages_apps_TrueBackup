package com.android.settings.dashboard

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

open class DashboardFragment : PreferenceFragmentCompat() {
    open fun getMetricsCategory(): Int = 0
    open fun getPreferenceScreenResId(): Int = 0
    open fun getLogTag(): String = "DashboardFragment"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val resId = getPreferenceScreenResId()
        if (resId != 0) {
            setPreferencesFromResource(resId, rootKey)
        }
    }
}
