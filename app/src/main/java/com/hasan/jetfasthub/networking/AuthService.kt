package com.hasan.jetfasthub.networking

import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.screens.login.model.AuthModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("authorizations")
    fun login(
        @Body authModel: AuthModel
    ): Call<AccessTokenModel>

    @FormUrlEncoded
    @POST("${Constants.BASIC_AUTH_URL}login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri : String
//        @Field("state") state: String,
//        @Field("redirect_uri") redirectUrl: String
    ) : Response<AccessTokenModel>

}