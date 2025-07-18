package com.zj.hometest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.zj.hometest.core.di.AppComponentProvider

class MainActivity : AppCompatActivity() {

    private val appComponent
        get() = (application as AppComponentProvider).appComponent

    private lateinit var router: Router

    private lateinit var intentRouter: IntentRouter

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentRouter.routeIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        router = Conductor.attachRouter(
            this, findViewById(R.id.controllerContainer), savedInstanceState
        )

        intentRouter = IntentRouter(router, appComponent.controllerRegistry)
        intentRouter.routeIntent(intent)

        supportActionBar?.hide()
    }
}