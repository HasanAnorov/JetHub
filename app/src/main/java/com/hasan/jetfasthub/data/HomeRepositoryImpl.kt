package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.home.authenticated_user.AuthenticatedUser
import com.hasan.jetfasthub.screens.main.home.received_events_model.ReceivedEventsModel
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN
import retrofit2.Response

interface HomeRepository {

    suspend fun getAuthenticatedUser(token: String): Response<AuthenticatedUser>
    suspend fun getUser(token: String, username: String): Response<GitHubUser>

    suspend fun getReceivedUserEvents(
        token: String,
        username: String,
    ): Response<ReceivedEventsModel>

}

class HomeRepositoryImpl(private val context: Context) : HomeRepository {

    override suspend fun getAuthenticatedUser(token: String): Response<AuthenticatedUser> {
        return RetrofitInstance(context = context).gitHubService.getAuthenticatedUser(
            token = "Bearer $PERSONAL_ACCESS_TOKEN"
        )
    }

    override suspend fun getUser(token: String, username: String): Response<GitHubUser> {
        return RetrofitInstance(context = context).gitHubService.getUser(
            authToken = PERSONAL_ACCESS_TOKEN,
            username = username
        )
    }

    override suspend fun getReceivedUserEvents(
        token: String, username: String
    ): Response<ReceivedEventsModel> {
        return RetrofitInstance(context).gitHubService.getReceivedUserEvents(
            authToken = PERSONAL_ACCESS_TOKEN,
            username = username,
        )
    }

}