package com.s8113459.assignment2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Application entry point. [HiltAndroidApp] triggers Hilt's code generation and creates the app-level DI container. */
@HiltAndroidApp
class App : Application()
