package com.s8113459.assignment2.di

import javax.inject.Qualifier

/** Dispatcher for blocking / network work. Tests replace it with a test dispatcher. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/** API base URL (from BuildConfig). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

/** Class location used in the auth path: "footscray", "sydney" or "br" (from BuildConfig). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthLocation
