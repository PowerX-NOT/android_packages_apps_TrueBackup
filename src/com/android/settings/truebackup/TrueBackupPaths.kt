/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.truebackup

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

object TrueBackupPaths {
    const val PACKAGE_RESTORE_CONFIG = "package_restore_config.json"

    /** Prefer [ApplicationInfo.loadIcon] so launcher-style icons resolve like the Settings app. */
    @JvmStatic
    fun loadPackageIcon(context: Context, pm: PackageManager, packageName: String): Drawable {
        return try {
            val info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            info.loadIcon(pm)
        } catch (_: PackageManager.NameNotFoundException) {
            context.getDrawable(android.R.drawable.sym_def_app_icon)!!
        }
    }

    @JvmStatic
    fun readFully(f: File): ByteArray {
        FileInputStream(f).use { input ->
            ByteArrayOutputStream().use { output ->
                val buf = ByteArray(16 * 1024)
                while (true) {
                    val n = input.read(buf)
                    if (n <= 0) break
                    output.write(buf, 0, n)
                }
                return output.toByteArray()
            }
        }
    }

    @JvmStatic
    fun resolveAppsDir(basePath: String?): File? {
        if (basePath == null) return null
        val base = File(basePath)

        if (base.isDirectory && base.name == "apps") {
            return base
        }

        if (base.isDirectory && base.name == "backup") {
            val candidate = File(base, "apps")
            if (candidate.isDirectory) return candidate
        }

        val candidate1 = File(File(base, "backup"), "apps")
        if (candidate1.isDirectory) return candidate1

        val candidate2 = File(base, "apps")
        if (candidate2.isDirectory) return candidate2

        return null
    }

    /** Best-effort: [dir] is under the configured backup tree (for UI paths that differ slightly). */
    @JvmStatic
    fun isUnderBackupBasePath(basePath: String, dir: File): Boolean {
        return try {
            val base = File(basePath).canonicalFile
            val leaf = dir.canonicalFile
            val basePathStr = base.path
            leaf.path == basePathStr ||
                leaf.path.startsWith(basePathStr + File.separator)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Ensures [packageDir] is a real directory under [appsRoot] (prevents deleting arbitrary paths).
     */
    @JvmStatic
    fun isBackupPackageDirUnderAppsRoot(appsRoot: File, packageDir: File): Boolean {
        return try {
            val root = appsRoot.canonicalFile
            val pkg = packageDir.canonicalFile
            pkg.isDirectory && (pkg.path == root.path || pkg.path.startsWith(root.path + File.separator))
        } catch (_: Exception) {
            false
        }
    }
}
