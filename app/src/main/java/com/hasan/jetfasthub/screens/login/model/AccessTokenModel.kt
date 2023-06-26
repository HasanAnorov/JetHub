package com.hasan.jetfasthub.screens.login.model

import com.google.gson.annotations.SerializedName

data class AccessTokenModel(
    @SerializedName("access_token")
    val access_token: String ? ,
)
