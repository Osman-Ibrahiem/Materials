package com.osman.materials.di

import com.osman.materials.data.executor.JobExecutor
import com.osman.materials.domain.diinterfaces.excuter.PostExecutionThread
import com.osman.materials.domain.diinterfaces.excuter.ThreadExecutor
import dagger.Module
import dagger.Provides

@Module
class AppDataModule {

    @Provides
    fun provideThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor {
        return jobExecutor
    }

    @Provides
    fun providePostExecutionThread(uiThread: UiThread): PostExecutionThread {
        return uiThread
    }

}