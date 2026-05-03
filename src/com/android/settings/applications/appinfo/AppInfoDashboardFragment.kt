package com.android.settings.applications.appinfo

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.Settings

open class AppInfoDashboardFragment {
    companion object {
        @JvmStatic
        fun startAppInfoFragment(
            @Suppress("UNUSED_PARAMETER") fragmentClass: Class<*>,
            appInfo: ApplicationInfo,
            context: Context,
            @Suppress("UNUSED_PARAMETER") metricsCategory: Int,
        ) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", appInfo.packageName, null)
            }
            context.startActivity(intent)
        }
    }
}
