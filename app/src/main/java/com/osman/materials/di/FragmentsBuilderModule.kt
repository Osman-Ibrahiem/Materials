package com.osman.materials.di


import com.osman.materials.ui.home.HomeFragment
import com.osman.materials.ui.splash.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentsBuilderModule {

    @ContributesAndroidInjector
    internal abstract fun bindSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    internal abstract fun bindHomeFragment(): HomeFragment
}