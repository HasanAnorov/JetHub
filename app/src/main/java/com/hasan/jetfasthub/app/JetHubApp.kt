package com.hasan.jetfasthub.app

import android.app.Application
import com.hasan.jetfasthub.di.appModule
import com.hasan.jetfasthub.di.basicAuthViewModelModule
import com.hasan.jetfasthub.di.commentEditModule
import com.hasan.jetfasthub.di.commitModule
import com.hasan.jetfasthub.di.homeModule
import com.hasan.jetfasthub.di.gistModule
import com.hasan.jetfasthub.di.gistsModule
import com.hasan.jetfasthub.di.notificationsModule
import com.hasan.jetfasthub.di.organisationModule
import com.hasan.jetfasthub.di.profileModule
import com.hasan.jetfasthub.di.repositoryModule
import com.hasan.jetfasthub.di.searchModule
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
                homeModule,
                repositoryModule,
                commitModule,
                commentEditModule,
                gistsModule,
                gistModule,
                notificationsModule,
                searchModule,
                profileModule,
                organisationModule,
                basicAuthViewModelModule,
            )
        }
    }

}