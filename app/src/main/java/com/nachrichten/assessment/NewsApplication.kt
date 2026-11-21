package com.nachrichten.assessment

import android.app.Application
import com.nachrichten.assessment.di.AppContainer
import com.nachrichten.assessment.di.DefaultAppContainer

class NewsApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
