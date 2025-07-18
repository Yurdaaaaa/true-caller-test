package com.zj.hometest.core.util.ext

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window

val Configuration.isDarkMode: Boolean
    get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

fun Window.setLightSystemBars(lightStatusBar: Boolean, lightNavBar: Boolean) {
    var visibility = decorView.systemUiVisibility
    if (lightStatusBar) {
        visibility = visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        visibility = visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }

    if (isOreo()) {
        if (lightNavBar) {
            visibility = visibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            visibility = visibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }
    decorView.systemUiVisibility = visibility
}

private fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

inline fun bundleOf(factory: Bundle.() -> Unit) = Bundle().apply(factory)