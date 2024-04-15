package com.trace.gtrack.data.persistence

import com.trace.gtrack.data.AppRepository
import com.trace.gtrack.data.IAppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindsPersistenceManager(persistenceManager: PersistenceManager): IPersistenceManager

    @Binds
    @Singleton
    fun bindsAppRepository(appRepository: AppRepository): IAppRepository

}
