package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.notifications.model.Notification
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsService {

    @Headers("Accept: application/vnd.github+json")
    @GET("notifications")
    suspend fun getAllNotifications(
        @Query("all") all: Boolean,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authToken: String,
    ): Response<Notification>

    @Headers("Accept: application/vnd.github+json")
    @GET("notifications")
    suspend fun getUnreadNotifications(
        @Header("Authorization") authToken: String,
        @Query("since") since: String
    ): Response<Notification>

    @Headers("Accept: application/vnd.github+json")
    @PATCH("notifications/threads/{thread_id}")
    suspend fun markAsRead(
        @Header("Authorization") authToken: String,
        @Path("thread_id") threadId: String
    ): Response<Int>

}