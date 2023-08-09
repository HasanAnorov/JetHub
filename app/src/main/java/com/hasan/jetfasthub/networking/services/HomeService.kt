package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.home.authenticated_user_model.AuthenticatedUser
import com.hasan.jetfasthub.screens.main.home.received_events_model.ReceivedEventsModel
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface HomeService {

    @Headers("Accept: application/vnd.github+json")
    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") token: String,
    ): Response<AuthenticatedUser>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/received_events")
    suspend fun getReceivedUserEvents(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<ReceivedEventsModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}")
    suspend fun getUser(
        @Header("Authorization") authToken: String,
        @Path("username") username: String
    ): Response<GitHubUser>

}