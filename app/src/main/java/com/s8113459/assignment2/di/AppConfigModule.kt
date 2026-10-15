package com.s8113459.assignment2.di

import com.s8113459.assignment2.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Configuration values and coroutine dispatchers. */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @AuthLocation
    fun provideAuthLocation(): String = BuildConfig.AUTH_LOCATION

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
