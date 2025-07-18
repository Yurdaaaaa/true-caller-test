package com.zj.hometest.lifeasandroidenginner.di

import com.zj.hometest.core.data.usecase.html.Html15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlEvery15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlPageFetchLifeAsAndroidEngineerUseCase
import com.zj.hometest.core.data.usecase.html.HtmlWordCounterUseCase
import com.zj.hometest.core.di.AppComponent
import com.zj.hometest.lifeasandroidenginner.LifeAsAndroidEngineerController
import com.zj.hometest.lifeasandroidenginner.LifeAsAndroidEngineerViewModel
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Scope

@LifeAsAndroidEngineerControllerScope
@Component(dependencies = [AppComponent::class], modules = [LifeAsAndroidEngineerComponentModule::class])
internal interface LifeAsAndroidEngineerControllerComponent {
    val lifeAsAndroidEngineerViewModel: LifeAsAndroidEngineerViewModel

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            @BindsInstance controller: LifeAsAndroidEngineerController,
        ): LifeAsAndroidEngineerControllerComponent
    }
}

@Module
object LifeAsAndroidEngineerComponentModule {

    @JvmStatic
    @Provides
    fun viewModel(
        controller: LifeAsAndroidEngineerController,
        mainThreadScheduler: Scheduler,
        fifteenthCharacterUseCase: Html15thCharacterUseCase,
        every15CharacterUseCase: HtmlEvery15thCharacterUseCase,
        wordCounterUseCase: HtmlWordCounterUseCase,
        htmlPageFetchLifeAsAndroidEngineerUseCase: HtmlPageFetchLifeAsAndroidEngineerUseCase
    ) = LifeAsAndroidEngineerViewModel(
        controller,
        fifteenthCharacterUseCase,
        every15CharacterUseCase,
        wordCounterUseCase,
        htmlPageFetchLifeAsAndroidEngineerUseCase,
        mainThreadScheduler
    )
}

@Scope
@Retention(AnnotationRetention.SOURCE)
private annotation class LifeAsAndroidEngineerControllerScope