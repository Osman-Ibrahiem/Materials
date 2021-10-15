package com.osman.materials.di.module

import android.content.Context
import com.osman.materials.domain.diinterfaces.AppContext
import dagger.Module
import okhttp3.logging.HttpLoggingInterceptor
import dagger.Provides
import java.io.File
import java.util.concurrent.TimeUnit

import okhttp3.*


@Module(includes = [ApplicationModule::class])
class OkHttpClientModule {

    @Provides
    fun cache(@AppContext context: Context): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong()
        return Cache(context.cacheDir, cacheSize)
    }


    @Provides
    fun okHttpClient(
        @AppContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val httpCacheDirectory = File(context.cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize.toLong())
        return OkHttpClient()
            .newBuilder()
            .cache(cache)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()

                request.header("Content-Type", "application/json")
                    .method(original.method, original.body)
                chain.proceed(request.build())
            }
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }


    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }


}