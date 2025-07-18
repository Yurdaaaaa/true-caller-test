package com.zj.hometest.core.di

import dagger.Module

@Module(
    includes = [
        NetModule::class,
        HtmlUseCaseModule::class,
        SchedulerModule::class
    ]
)
class AppModule