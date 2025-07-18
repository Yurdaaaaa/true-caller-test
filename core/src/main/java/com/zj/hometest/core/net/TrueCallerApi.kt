package com.zj.hometest.core.net

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface TrueCallerApi {

    @Streaming // in case page is too long
    @GET("life-as-an-android-engineer")
    fun downloadHtmlBody(): Observable<ResponseBody>
}