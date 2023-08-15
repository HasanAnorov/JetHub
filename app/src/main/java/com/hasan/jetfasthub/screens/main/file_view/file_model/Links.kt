package com.hasan.jetfasthub.screens.main.file_view.file_model


import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("git")
    val git: String,
    @SerializedName("html")
    val html: String,
    @SerializedName("self")
    val self: String
)