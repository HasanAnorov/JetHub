package com.hasan.jetfasthub.screens.main.home

import android.content.Context
import android.util.Log
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.home.events.models.Events
import retrofit2.Response

interface HomeRepository {
    suspend fun getUserEvents(
        token: String,
        username: String,
        //page: Int
    ): Response<Events>
}

class HomeRepositoryImpl(private val context: Context) : HomeRepository {

    override suspend fun getUserEvents(
        token: String,
        username: String,
       // page: Int
    ): Response<Events> {

        Log.d("ahi3646", "getUserEvents in repoImpl : $token - - $username - - ")

        return RetrofitInstance(context).homeService.getUserEvents(
            authToken = "Bearer $token",
            username = username,
            //page = page
        )
    }

}