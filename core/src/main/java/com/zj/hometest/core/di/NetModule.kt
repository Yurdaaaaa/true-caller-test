package com.zj.hometest.core.di

import android.content.Context
import android.content.pm.PackageManager
import com.zj.hometest.core.BuildConfig
import com.zj.hometest.core.net.Constants
import com.zj.hometest.core.net.TrueCallerApi
import com.zj.hometest.core.net.TrueCallerApiClient
import com.zj.hometest.core.net.TrueCallerNetworkManager
import com.zj.hometest.core.net.interceptor.IdempotencyInterceptor
import com.zj.hometest.core.net.interceptor.UserAgentInterceptor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


@Module
object NetModule {

    @JvmStatic
    private fun userAgent(context: Context): String {
        val packageName = context.packageName ?: "hometest"

        val packageInfo = try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

        val versionName = packageInfo?.versionName ?: "-1"
        val versionCode = packageInfo?.versionCode ?: -1

        return "$packageName/v$versionName(vc$versionCode)/Android"
    }

    @AppScope
    @Provides
    @JvmStatic
    @BaseOkHttp
    fun baseOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addNetworkInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()

    @AppScope
    @Provides
    @JvmStatic
    @TrueCallerOkHttp
    fun trueCallerOkHttp(
        @BaseOkHttp baseOkHttpClient: OkHttpClient,
        context: Context
    ): OkHttpClient {
        return baseOkHttpClient.newBuilder().apply {
            connectTimeout(40, TimeUnit.SECONDS)
            readTimeout(40, TimeUnit.SECONDS)
            writeTimeout(40, TimeUnit.SECONDS)

            addInterceptor(UserAgentInterceptor(userAgent(context)))
            addInterceptor(IdempotencyInterceptor()) // all request are @GET so this is redundant
        }.build()
    }

    @AppScope
    @Provides
    @JvmStatic
    fun trueCallerApiClient(
        @TrueCallerOkHttp trueCallerOkHttp: OkHttpClient
    ): TrueCallerApiClient {

        val retrofit = Retrofit.Builder().baseUrl(
            if (BuildConfig.DEBUG) {
                Constants.API_DEBUG_TRUE_CALLER
            } else {
                Constants.API_PROD_TRUE_CALLER
            }
        )
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(trueCallerOkHttp).build()

        val api = retrofit.create(TrueCallerApi::class.java)
        return TrueCallerApiClient(api)
    }

    @AppScope
    @Provides
    @JvmStatic
    fun rxJava2CallAdapterFactory(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    @AppScope
    @JvmStatic
    @Provides
    fun trueCallerNetworkManager(
        trueCallerApiClient: TrueCallerApiClient,
        @IoScheduler ioThreadScheduler: Scheduler
    ) = TrueCallerNetworkManager(
        trueCallerApiClient,
        ioThreadScheduler
    )
}