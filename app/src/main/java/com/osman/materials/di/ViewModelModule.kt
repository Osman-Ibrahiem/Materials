package com.osman.materials.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.osman.materials.di.module.DaggerViewModelFactory
import com.osman.materials.di.module.ViewModelKey
import com.osman.materials.ui.home.HomeFragment
import com.osman.materials.ui.home.MaterialsViewModel
import com.osman.materials.ui.splash.SplashFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MaterialsViewModel::class)
    abstract fun bindMaterialsViewModel(viewModel: MaterialsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

}