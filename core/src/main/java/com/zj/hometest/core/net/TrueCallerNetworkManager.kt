package com.zj.hometest.core.net

import com.zj.hometest.core.util.ext.asEvents
import io.reactivex.Observable
import io.reactivex.Scheduler

class TrueCallerNetworkManager(
    private val trueCallerApiClient: TrueCallerApiClient,
    private val ioThreadScheduler: Scheduler
) {

    fun getLifeAsDeveloperHtmlBody(): Observable<DownloadHtmlEvent> {
        return trueCallerApiClient.downloadHtmlBody()
            .asEvents(
                started = {
                    DownloadHtmlEvent.Started
                },
                success = {
                    DownloadHtmlEvent.Success(
                        body = it
                    )
                },
                errorr = {
                    DownloadHtmlEvent.Error(
                        error = it
                    )
                }
            )
            .subscribeOn(ioThreadScheduler)
    }

    sealed class DownloadHtmlEvent {
        object Started : DownloadHtmlEvent()
        data class Success(val body: String) : DownloadHtmlEvent()
        data class Error(val error: Throwable) : DownloadHtmlEvent()
    }
}