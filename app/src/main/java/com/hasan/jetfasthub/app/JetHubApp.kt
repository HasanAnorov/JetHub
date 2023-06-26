package com.hasan.jetfasthub.app

import android.app.Application
import com.hasan.jetfasthub.di.appModule
import com.hasan.jetfasthub.di.basicAuthViewModelModule
import com.hasan.jetfasthub.di.eventsModule
import com.hasan.jetfasthub.di.homeViewModelModule
import com.hasan.jetfasthub.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class JetHubApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@JetHubApp)
            modules(
                appModule,
                eventsModule,
                profileModule,
                basicAuthViewModelModule,
                homeViewModelModule
            )
        }
    }

}