package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.data.model.AccessTokenModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response


interface AuthRepository {
    suspend fun getAccessToken(code: String): Response<AccessTokenModel>
}

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    override suspend fun getAccessToken(code: String): Response<AccessTokenModel> {
        return RetrofitInstance(context).authService.getAccessToken(
            code = code,
            Constants.CLIENT_ID,
            Constants.CLIENT_SECRET,
//                GitHubHelper.STATE,
//                GitHubHelper.REDIRECT_URL
        )
    }

}