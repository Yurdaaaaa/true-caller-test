package com.zj.hometest.di

import com.zj.hometest.ControllerRegistryImpl
import com.zj.hometest.core.ControllerRegistry
import dagger.Module
import dagger.Provides

@Module
object AppAppModule {

    @Provides
    @JvmStatic
    fun controllerRegistry(): ControllerRegistry = ControllerRegistryImpl()
}