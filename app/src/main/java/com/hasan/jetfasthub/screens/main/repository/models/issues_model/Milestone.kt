package com.hasan.jetfasthub.screens.main.repository.models.issues_model


import com.google.gson.annotations.SerializedName

data class Milestone(
    @SerializedName("closed_at")
    val closedAt: String,
    @SerializedName("closed_issues")
    val closedIssues: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("creator")
    val creator: Creator,
    @SerializedName("description")
    val description: String,
    @SerializedName("due_on")
    val dueOn: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("labels_url")
    val labelsUrl: String,
    @SerializedName("node_id")
    val nodeId: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("open_issues")
    val openIssues: Int,
    @SerializedName("state")
    val state: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("url")
    val url: String
)