package com.zj.hometest

import android.content.Intent
import com.bluelinelabs.conductor.Router
import com.zj.hometest.core.ControllerRegistry
import com.zj.hometest.core.util.LOG
import com.zj.hometest.core.util.ext.noAnimSetRoot

class IntentRouter(private val router: Router, private val controllerRegistry: ControllerRegistry) {

    fun routeIntent(intent: Intent) {
        val uri = intent.data
        if (uri != null) {
            // if app is installed intent.data will be filled or action send from system, click on push etc..
            // handle non null uri
        } else {
            handleDefault()
        }
    }

    private fun handleDefault() {
        if (!router.hasRootController()) {
            LOG.d("handleDefault -> showing lifeAsAndroidEngineerController screen")
            router.noAnimSetRoot(controllerRegistry.lifeAsAndroidEngineerController())
        }
    }
}