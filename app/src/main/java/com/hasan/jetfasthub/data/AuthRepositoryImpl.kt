package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.screens.main.home.data.remote.authenticated_user_model.AuthenticatedUser
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface AuthRepository {

    suspend fun getAccessToken(code: String): Response<AccessTokenModel>

    suspend fun getAuthenticatedUser(token: String): Response<AuthenticatedUser>

}

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    override suspend fun getAccessToken(code: String): Response<AccessTokenModel> {
        return RestClient(context).authorizationService.getAccessToken(
            clientId = Constants.CLIENT_ID,
            clientSecret = Constants.CLIENT_SECRET,
            code = code,
        )
    }

    override suspend fun getAuthenticatedUser(token: String): Response<AuthenticatedUser> {
        return RestClient(context = context).authorizationService.getAuthenticatedUser(
            token = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}"
        )
    }

}