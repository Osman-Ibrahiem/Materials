package com.osman.materials.di.module

import com.osman.materials.di.DataModule
import com.osman.materials.di.FragmentsBuilderModule
import com.osman.materials.di.ViewModelModule
import com.osman.materials.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentsBuilderModule::class])
    internal abstract fun mainActivity(): MainActivity


}