package com.android.settingslib.widget

import android.content.Context
import androidx.preference.CheckBoxPreference

open class SelectorWithWidgetPreference(
    context: Context,
    @Suppress("UNUSED_PARAMETER") appendWidgetLayout: Boolean,
) : CheckBoxPreference(context) {

    private var clickListener: ((SelectorWithWidgetPreference) -> Unit)? = null

    fun setOnClickListener(listener: (SelectorWithWidgetPreference) -> Unit) {
        clickListener = listener
    }

    override fun onClick() {
        val listener = clickListener
        if (listener != null) {
            listener(this)
        } else {
            super.onClick()
        }
    }
}
