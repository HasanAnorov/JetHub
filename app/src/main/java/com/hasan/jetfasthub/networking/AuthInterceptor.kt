package com.hasan.jetfasthub.networking

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.utility.Constants
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    private lateinit var response: Response
    private lateinit var token: AccessTokenModel
    private val gson = Gson()
    private val type = object : TypeToken<AccessTokenModel>() {}.type

    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        PreferenceHelper.getToken(context = context).let { access_token ->
            token = gson.fromJson(access_token, type)
        }

        val refreshRequest = newRequestBuilder(
            original,
            "Accept: ",
            " application/json"
        )

        response = if (original.url.toString()
                .contains("/oauth2/token") || token.access_token.isNullOrEmpty()
        ) {
            //firs attempt or refresh method
            Log.d("RESPONSE_REFRESH", "refreshing")
            chain.proceed(refreshRequest.build())
        } else {
            //normal state
            Log.d("NORMAL_STATE", "token not expired")
            chain.proceed(newRequestBuilder(original, "Bearer", token.access_token!!).build())
        }

        /**
        if (response.code == 401) {
        if (token.refresh_token?.isNotEmpty()!!) {
        val reqBody = FormBody.Builder()
        .add(Constants.GRANT_TYPE, Constants.REFRESH_TOKEN)
        .add(Constants.REFRESH_TOKEN, token.refresh_token!!)
        .build()

        val modelRequest = RefreshRequest("refresh_token",token.refresh_token.toString())
        val stringModel = gson.toJson(modelRequest)
        Log.d("STRING_MODEL",stringModel)

        Log.d("REFRESH_TOKEN",token.refresh_token.toString())
        val newRequest = newRequestBuilder(
        Request.Builder().url("${Constants.BASE_URL}oauth2/refresh").build(),
        Credentials.basic(CLIENT_ID, CLIENT_SECRET))
        .post(
        RequestBody.create(
        "application/json;charset=utf-8".toMediaTypeOrNull(),
        stringModel)
        )
        .build()

        response.close()

        val newResponse = chain.proceed(newRequest)

        if (newResponse.code == 200) {
        val oauthToken = GsonBuilder()
        .create()
        .fromJson(newResponse.body?.string(), Token::class.java)
        localData.refreshToken(oauthToken.access_token.toString())
        newResponse.close()
        return chain.proceed(newRequestBuilder(original, "Bearer " + oauthToken.access_token!!).build())
        } else{
        Log.d("","Error in Auth Interceptor, code ${newResponse.code}")
        }
        } else {
        val modelRequest = RefreshRequest("refresh_token",token.refresh_token.toString())
        val stringModel = gson.toJson(modelRequest)

        val reqBody = FormBody.Builder()
        .add(Constants.GRANT_TYPE, "client_credentials")
        .build()

        val newRequest = newRequestBuilder(
        Request.Builder().url("${Constants.BASE_URL}oauth2/refresh").build(),
        Credentials.basic(CLIENT_ID, CLIENT_SECRET))
        .post(RequestBody.create("application/json;charset=utf-8".toMediaTypeOrNull(), stringModel)).build()

        response.close()

        val newResponse = chain.proceed(newRequest)

        if (newResponse.code == 200) {
        val oauthToken = GsonBuilder().create()
        .fromJson(newResponse.body!!.string(), Token::class.java)

        return chain.proceed(newRequestBuilder(original, "Bearer " + oauthToken.access_token!!).build())

        }
        }
        }
         */

        return response
    }

    private fun newRequestBuilder(
        request: Request,
        headerName: String,
        headerValue: String
    ): Request.Builder {
        return request.newBuilder()
            //.header("Content-Type","application/json")
            .header(headerName, headerValue)
    }

}