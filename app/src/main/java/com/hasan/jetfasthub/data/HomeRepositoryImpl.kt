package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.home.events.models.Events
import com.hasan.jetfasthub.screens.main.home.events.received_model.ReceivedEvents
import retrofit2.Response

interface HomeRepository {
    suspend fun getUserEvents(
        token: String,
        username: String,
    ): Response<Events>

    suspend fun getReceivedUserEvents(
        token: String,
        username: String,
    ): Response<ReceivedEvents>

}

class HomeRepositoryImpl(private val context: Context) : HomeRepository {

    override suspend fun getUserEvents(
        token: String,
        username: String,
    ): Response<Events> {

        return RetrofitInstance(context).homeService.getUserEvents(
            authToken = "Bearer $token",
            username = username,
        )
    }

    override suspend fun getReceivedUserEvents(
        token: String,
        username: String
    ): Response<ReceivedEvents> {
        return RetrofitInstance(context).homeService.getReceivedUserEvents(
            authToken = token,
            username = username,
        )
    }

}