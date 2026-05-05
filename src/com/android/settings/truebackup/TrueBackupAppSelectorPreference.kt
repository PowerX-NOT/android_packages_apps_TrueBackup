/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.truebackup

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.preference.PreferenceViewHolder
import com.android.settingslib.widget.SelectorWithWidgetPreference
import com.android.truebackup.R

/**
 * Uses AOSP [SelectorWithWidgetPreference] layout (checkbox, icon, title, optional end affordance)
 * like the Settings app. Selection toggles from the checkbox; navigation uses the extra widget.
 */
class TrueBackupAppSelectorPreference(context: Context) :
    SelectorWithWidgetPreference(context, true) {

    var onContentClick: (() -> Unit)? = null
        set(value) { field = value }

    init {
        // Use a custom row where the checkbox widget is on the right end.
        layoutResource = R.layout.true_backup_preference_app_selector_row
        // We navigate from row tap now; no separate right-side arrow target.
        setExtraWidgetOnClickListener(null)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.apply {
            isClickable = onContentClick != null && isEnabled
            isFocusable = isClickable
            setOnClickListener(if (isClickable) View.OnClickListener { onContentClick?.invoke() } else null)
        }

        val widgetFrame = holder.findViewById(android.R.id.widget_frame)

        if (!isEnabled) {
            widgetFrame?.isClickable = false
            widgetFrame?.isFocusable = false
            widgetFrame?.setOnClickListener(null)
            widgetFrame?.background = null
            disableExtraWidget(holder)
            return
        }

        widgetFrame?.isClickable = true
        widgetFrame?.isFocusable = true
        widgetFrame?.setOnClickListener { onClick() }
    }

    private fun disableExtraWidget(holder: PreferenceViewHolder) {
        val extra = holder.findViewById(R.id.selector_extra_widget) as? ImageView
        val container = holder.findViewById(R.id.selector_extra_widget_container)
        extra?.isClickable = false
        extra?.isFocusable = false
        extra?.setOnClickListener(null)
        container?.isClickable = false
        container?.isFocusable = false
    }
}
