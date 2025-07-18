package com.zj.hometest.core.net

import io.reactivex.Observable
import okhttp3.ResponseBody

class TrueCallerApiClient(private val api: TrueCallerApi) {

    fun downloadHtmlBody(): Observable<String> {
        return api.downloadHtmlBody().map { it.string() }
    }
}