package com.zj.hometest.core.ui

import android.view.View
import com.zj.hometest.core.view.TintToolbar
import com.zj.hometest.core.R

open class ViewBinding(view: View) {
    val toolbar: TintToolbar? = view.findViewById(R.id.toolbar)
}