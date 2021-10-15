package com.osman.materials.di

import com.osman.materials.MyApp
import com.osman.materials.di.module.ActivityBuilderModule
import com.osman.materials.di.module.ApplicationModule
import com.osman.materials.di.module.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ActivityBuilderModule::class,
        AndroidInjectionModule::class,
        AppDataModule::class,
        ApplicationModule::class,
        NetworkModule::class,
        DataModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<MyApp> {

    override fun inject(instance: MyApp)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: MyApp): Builder
    }
}