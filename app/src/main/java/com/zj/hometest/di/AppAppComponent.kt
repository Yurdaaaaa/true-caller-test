package com.zj.hometest.di

import android.app.Application
import android.content.Context
import com.zj.hometest.core.di.AppModule
import com.zj.hometest.core.di.AppScope
import com.zj.hometest.core.di.AppComponent
import dagger.BindsInstance
import dagger.Component
import io.reactivex.Scheduler

@AppScope
@Component(
    modules = [
        AppAppModule::class,
        AppModule::class
    ]
)
interface AppAppComponent : AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance app: Application,
            @BindsInstance scheduler: Scheduler
        ): AppAppComponent
    }
}