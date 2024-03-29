package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.screens.main.home.data.remote.authenticated_user_model.AuthenticatedUser
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthorizationService {

    @FormUrlEncoded
    @POST("${Constants.BASIC_AUTH_URL}login/oauth/access_token")
    @Headers(
        "Accept: application/json",
    )
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        //other parameters are optional
    ): Response<AccessTokenModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") token: String,
    ): Response<AuthenticatedUser>

}