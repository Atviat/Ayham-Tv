package com.ayham.tv.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap?,
)

/** Discovers and launches the apps installed on the TV. */
class AppRepository(private val context: Context) {

    private val pm: PackageManager = context.packageManager

    suspend fun loadApps(): List<AppInfo> = withContext(Dispatchers.Default) {
        val seen = HashSet<String>()
        val result = ArrayList<AppInfo>()

        for (category in listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (info in pm.queryIntentActivities(intent, 0)) {
                val pkg = info.activityInfo.packageName
                if (pkg == context.packageName || !seen.add(pkg)) continue
                val label = info.loadLabel(pm).toString()
                val icon = runCatching {
                    (info.activityInfo.loadBanner(pm) ?: info.loadIcon(pm))
                        .toBitmap(width = ICON_SIZE, height = ICON_SIZE)
                        .asImageBitmap()
                }.getOrNull()
                result.add(AppInfo(pkg, label, icon))
            }
        }
        result.sortedBy { it.label.lowercase() }
    }

    /** Returns true if the app was started. */
    fun launch(packageName: String): Boolean {
        val intent = pm.getLeanbackLaunchIntentForPackage(packageName)
            ?: pm.getLaunchIntentForPackage(packageName)
            ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching { context.startActivity(intent) }.isSuccess
    }

    private companion object {
        const val ICON_SIZE = 160
    }
}
