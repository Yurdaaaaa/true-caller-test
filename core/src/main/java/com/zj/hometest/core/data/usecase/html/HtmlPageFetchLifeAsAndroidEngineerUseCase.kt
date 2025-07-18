package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.NetworkUseCaseObservable
import com.zj.hometest.core.net.TrueCallerNetworkManager
import com.zj.hometest.core.net.TrueCallerNetworkManager.*
import io.reactivex.Observable

class HtmlPageFetchLifeAsAndroidEngineerUseCase(
    private val networkManager: TrueCallerNetworkManager
) : NetworkUseCaseObservable<DownloadHtmlEvent> {

    override fun execute(): Observable<DownloadHtmlEvent> {
        return networkManager.getLifeAsDeveloperHtmlBody()
    }
}