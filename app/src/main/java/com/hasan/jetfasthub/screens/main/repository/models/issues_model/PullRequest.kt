package com.hasan.jetfasthub.screens.main.repository.models.issues_model


import com.google.gson.annotations.SerializedName

data class PullRequest(
    @SerializedName("diff_url")
    val diffUrl: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("patch_url")
    val patchUrl: String,
    @SerializedName("url")
    val url: String
)