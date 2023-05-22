package com.hasan.jetfasthub.networking

import com.hasan.jetfasthub.data.model.AccessTokenModel
import com.hasan.jetfasthub.data.model.AuthModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("authorizations")
    fun login(
        @Body authModel: AuthModel
    ): Call<AccessTokenModel>

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
//        @Field("state") state: String,
//        @Field("redirect_uri") redirectUrl: String
    ) : Response<AccessTokenModel>

}
