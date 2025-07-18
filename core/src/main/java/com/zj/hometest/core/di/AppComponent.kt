package com.zj.hometest.core.di

import android.content.Context
import com.zj.hometest.core.ControllerRegistry
import com.zj.hometest.core.data.usecase.html.Html15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlEvery15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlPageFetchLifeAsAndroidEngineerUseCase
import com.zj.hometest.core.data.usecase.html.HtmlWordCounterUseCase
import com.zj.hometest.core.net.TrueCallerNetworkManager
import io.reactivex.Scheduler
import okhttp3.OkHttpClient

interface AppComponent {
    val context: Context
    val mainThreadScheduler: Scheduler
    @get:ComputationScheduler val computationScheduler: Scheduler
    @get:IoScheduler val ioScheduler: Scheduler
    @get:BaseOkHttp val baseOkHttpClient: OkHttpClient
    @get:TrueCallerOkHttp val trueCallerOkHttp: OkHttpClient
    val trueCallerNetworkManager: TrueCallerNetworkManager
    val controllerRegistry: ControllerRegistry
    val html15thCharacterUseCase: Html15thCharacterUseCase
    val htmlEvery15thCharacterUseCase: HtmlEvery15thCharacterUseCase
    val htmlWordCounterUseCase: HtmlWordCounterUseCase
    val htmlPageFetchLifeAsAndroidEngineerUseCase: HtmlPageFetchLifeAsAndroidEngineerUseCase
}