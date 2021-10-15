package com.osman.materials.di

import com.osman.materials.data.datasource.IFakeDataSource
import com.osman.materials.data.datasource.IRemoteDataSource
import com.osman.materials.data.executor.JobExecutor
import com.osman.materials.data.datasource.fake.source.FakeDataSourceImp
import com.osman.materials.data.datasource.remote.service.IService
import com.osman.materials.data.datasource.remote.source.RemoteDataSourceImp
import com.osman.materials.data.repository.RepositoryImp
import com.osman.materials.domain.IRepository
import com.osman.materials.domain.diinterfaces.excuter.PostExecutionThread
import com.osman.materials.domain.diinterfaces.excuter.ThreadExecutor
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class DataModule {

    @Provides
    fun provideIRepository(repository: RepositoryImp): IRepository {
        return repository
    }

    @Provides
    fun provideRemoteDataSource(remoteDataSource: RemoteDataSourceImp): IRemoteDataSource {
        return remoteDataSource
    }

    @Provides
    fun provideFakeDataSource(fakeDataSource: FakeDataSourceImp): IFakeDataSource {
        return fakeDataSource
    }

    @Provides
    fun provideService(retrofit: Retrofit): IService {
        return retrofit.create(IService::class.java)
    }

}