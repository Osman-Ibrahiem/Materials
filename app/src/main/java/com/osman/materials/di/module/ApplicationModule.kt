package com.osman.materials.di.module

import android.content.Context
import com.osman.materials.BuildConfig
import com.osman.materials.MyApp
import com.osman.materials.domain.diinterfaces.AppContext
import com.osman.materials.domain.diinterfaces.AppRemoteUrl
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule {

    @AppRemoteUrl
    @Provides
    fun serviceURl(): String {
        return BuildConfig.DOMAIN
    }


    @AppContext
    @Provides
    fun context(application: MyApp): Context {
        return application.applicationContext
    }

}