package com.hasan.jetfasthub.networking

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.hasan.jetfasthub.networking.services.GistService
import com.hasan.jetfasthub.networking.services.HomeService
import com.hasan.jetfasthub.networking.services.NotificationsService
import com.hasan.jetfasthub.networking.services.OrganisationService
import com.hasan.jetfasthub.networking.services.ProfileService
import com.hasan.jetfasthub.utility.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClient(context: Context) {

    private val retrofit by lazy {

        val client = OkHttpClient.Builder()
            .addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )
            //.addInterceptor(AuthenticationInterceptor())
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    val gitHubService: GitHubService by lazy {
        retrofit.create(GitHubService::class.java)
    }

    val notificationsService: NotificationsService by lazy {
        retrofit.create(NotificationsService::class.java)
    }

    val organisationService: OrganisationService by lazy {
        retrofit.create(OrganisationService::class.java)
    }

    val homeService: HomeService by lazy {
        retrofit.create(HomeService::class.java)
    }

    val profileGist: ProfileService by lazy {
        retrofit.create(ProfileService::class.java)
    }

    val gistService: GistService by lazy {
        retrofit.create(GistService::class.java)
    }

}
