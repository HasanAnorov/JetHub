package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.screens.login.model.AuthModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRestService {

    @POST("authorizations")
    fun login(
        @Body authModel: AuthModel
    ): Call<AccessTokenModel>

}