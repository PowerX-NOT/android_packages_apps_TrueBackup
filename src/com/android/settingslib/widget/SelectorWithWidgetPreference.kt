package com.android.settingslib.widget

import android.content.Context
import androidx.preference.CheckBoxPreference

open class SelectorWithWidgetPreference(
    context: Context,
    @Suppress("UNUSED_PARAMETER") appendWidgetLayout: Boolean,
) : CheckBoxPreference(context)
