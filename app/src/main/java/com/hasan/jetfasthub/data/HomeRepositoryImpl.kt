package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.home.received_model.ReceivedEvents
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.utility.Constants.PERSONAL_ACCESS_TOKEN
import retrofit2.Response

interface HomeRepository {

    suspend fun getUser(token: String, username: String): Response<GitHubUser>

    suspend fun getReceivedUserEvents(
        token: String,
        username: String,
    ): Response<ReceivedEvents>

}

class HomeRepositoryImpl(private val context: Context) : HomeRepository {

    override suspend fun getUser(token: String, username: String): Response<GitHubUser> {
        return RetrofitInstance(context = context).gitHubService.getUser(
//            authToken = token,
            authToken = PERSONAL_ACCESS_TOKEN,
            username = username
        )
    }

    override suspend fun getReceivedUserEvents(
        token: String, username: String
    ): Response<ReceivedEvents> {
        return RetrofitInstance(context).gitHubService.getReceivedUserEvents(
//            authToken = token,
            authToken = PERSONAL_ACCESS_TOKEN,
            username = username,
        )
    }

}