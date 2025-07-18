package com.zj.hometest.core.di

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

@Module
object SchedulerModule {

    @Provides
    @IoScheduler
    @JvmStatic
    fun ioThreadScheduler(): Scheduler = Schedulers.io()

    @Provides
    @ComputationScheduler
    @JvmStatic
    fun computationThreadScheduler(): Scheduler = Schedulers.computation() // computation have fixed-size thread pool -> ensures parallelization
}