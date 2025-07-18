package com.zj.hometest.core.di

import javax.inject.Qualifier
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TrueCallerOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoScheduler

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComputationScheduler