package com.hasan.jetfasthub.networking

import com.hasan.jetfasthub.screens.main.home.events.models.Events
import com.hasan.jetfasthub.screens.main.home.events.received_model.ReceivedEvents
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeService {

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/events")
    suspend fun getUserEvents(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<Events>

    @Headers("Accept: application/vnd.github+json")
    @GET("users/{username}/received_events")
    suspend fun getReceivedUserEvents(
        @Header("Authorization") authToken: String,
        @Path("username") username: String,
    ): Response<ReceivedEvents>

}