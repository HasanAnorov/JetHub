package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.profile.model.User
import retrofit2.Response

interface ProfileRepository {
    suspend fun getUser(token: String, username: String): Response<User>
}

class ProfileRepositoryImpl(private val context: Context) : ProfileRepository {
    override suspend fun getUser(token: String, username: String): Response<User> {
        return RetrofitInstance(context = context).profileService.getUser(
            authToken = "Bearer $token", username = username
        )
    }
}