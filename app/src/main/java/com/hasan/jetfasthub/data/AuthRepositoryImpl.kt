package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface AuthRepository {

    suspend fun getAccessToken(code: String): Response<AccessTokenModel>

}

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    override suspend fun getAccessToken(code: String): Response<AccessTokenModel> {
        return RestClient(context).authorizationService.getAccessToken(
            clientId = Constants.CLIENT_ID,
            clientSecret = Constants.CLIENT_SECRET,
            code = code,
        )
    }
}