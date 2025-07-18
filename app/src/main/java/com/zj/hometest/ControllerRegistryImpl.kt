package com.zj.hometest

import com.bluelinelabs.conductor.Controller
import com.zj.hometest.core.ControllerRegistry
import com.zj.hometest.lifeasandroidenginner.LifeAsAndroidEngineerController

class ControllerRegistryImpl : ControllerRegistry {

    override fun lifeAsAndroidEngineerController(): Controller = LifeAsAndroidEngineerController()
}