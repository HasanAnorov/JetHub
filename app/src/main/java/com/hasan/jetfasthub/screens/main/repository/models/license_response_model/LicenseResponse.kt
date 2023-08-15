package com.hasan.jetfasthub.screens.main.repository.models.license_response_model


import com.google.gson.annotations.SerializedName

data class LicenseResponse(
    @SerializedName("body")
    val body: String,
    @SerializedName("conditions")
    val conditions: List<String>,
    @SerializedName("description")
    val description: String,
    @SerializedName("featured")
    val featured: Boolean,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("implementation")
    val implementation: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("limitations")
    val limitations: List<String>,
    @SerializedName("name")
    val name: String,
    @SerializedName("node_id")
    val nodeId: String,
    @SerializedName("permissions")
    val permissions: List<String>,
    @SerializedName("spdx_id")
    val spdxId: String,
    @SerializedName("url")
    val url: String
)