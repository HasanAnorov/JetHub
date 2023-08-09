package com.hasan.jetfasthub.networking

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.hasan.jetfasthub.networking.services.AuthorizationService
import com.hasan.jetfasthub.networking.services.CommentService
import com.hasan.jetfasthub.networking.services.CommitService
import com.hasan.jetfasthub.networking.services.GistService
import com.hasan.jetfasthub.networking.services.HomeService
import com.hasan.jetfasthub.networking.services.NotificationsService
import com.hasan.jetfasthub.networking.services.OrganisationService
import com.hasan.jetfasthub.networking.services.ProfileService
import com.hasan.jetfasthub.networking.services.RepositoryService
import com.hasan.jetfasthub.networking.services.SearchService
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
            //.addInterceptor(AuthenticationInterceptor(context))
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

    val repositoryService: RepositoryService by lazy {
        retrofit.create(RepositoryService::class.java)
    }

    val commitService: CommitService by lazy {
        retrofit.create(CommitService::class.java)
    }

    val commentService: CommentService by lazy {
        retrofit.create(CommentService::class.java)
    }

    val authorizationService: AuthorizationService by lazy {
        retrofit.create(AuthorizationService::class.java)
    }

    val searchService: SearchService by lazy {
        retrofit.create(SearchService::class.java)
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

    val profileService: ProfileService by lazy {
        retrofit.create(ProfileService::class.java)
    }

    val gistService: GistService by lazy {
        retrofit.create(GistService::class.java)
    }

}
