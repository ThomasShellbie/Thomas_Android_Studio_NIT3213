package com.s8113459.assignment2.di

import com.s8113459.assignment2.data.repository.AuthRepositoryImpl
import com.s8113459.assignment2.data.repository.DashboardRepositoryImpl
import com.s8113459.assignment2.domain.repository.AuthRepository
import com.s8113459.assignment2.domain.repository.DashboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds repository interfaces to their implementations, so ViewModels depend only on abstractions. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
}
