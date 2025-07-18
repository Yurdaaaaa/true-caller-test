package com.zj.hometest.core.di

import com.zj.hometest.core.data.usecase.html.Html15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlEvery15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlPageFetchLifeAsAndroidEngineerUseCase
import com.zj.hometest.core.data.usecase.html.HtmlWordCounterUseCase
import com.zj.hometest.core.net.TrueCallerNetworkManager
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler

@Module
object HtmlUseCaseModule {

    @AppScope
    @Provides
    @JvmStatic
    fun html15thCharacterUseCase(@ComputationScheduler computationThreadScheduler: Scheduler): Html15thCharacterUseCase {
        return Html15thCharacterUseCase(computationThreadScheduler)
    }

    @AppScope
    @Provides
    @JvmStatic
    fun htmlEvery15thCharacterUseCase(@ComputationScheduler computationThreadScheduler: Scheduler): HtmlEvery15thCharacterUseCase {
        return HtmlEvery15thCharacterUseCase(computationThreadScheduler)
    }

    @AppScope
    @Provides
    @JvmStatic
    fun htmlWordCounterUseCase(@ComputationScheduler computationThreadScheduler: Scheduler): HtmlWordCounterUseCase {
        return HtmlWordCounterUseCase(computationThreadScheduler)
    }

    @AppScope
    @Provides
    @JvmStatic
    fun htmlPageFetchLifeAsAndroidEngineerUseCase(
        trueCallerNetworkManager: TrueCallerNetworkManager
    ): HtmlPageFetchLifeAsAndroidEngineerUseCase {
        return HtmlPageFetchLifeAsAndroidEngineerUseCase(trueCallerNetworkManager)
    }
}