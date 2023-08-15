package com.hasan.jetfasthub.screens.main.repository.models.issues_model


import com.google.gson.annotations.SerializedName

data class RepoIssuesModelItem(
    @SerializedName("active_lock_reason")
    val activeLockReason: String,
    @SerializedName("assignee")
    val assignee: Assignee,
    @SerializedName("assignees")
    val assignees: List<Assignee>,
    @SerializedName("author_association")
    val authorAssociation: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("closed_at")
    val closedAt: Any,
    @SerializedName("closed_by")
    val closedBy: ClosedBy,
    @SerializedName("comments")
    val comments: Int,
    @SerializedName("comments_url")
    val commentsUrl: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("events_url")
    val eventsUrl: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("labels")
    val labels: List<Label>,
    @SerializedName("labels_url")
    val labelsUrl: String,
    @SerializedName("locked")
    val locked: Boolean,
    @SerializedName("milestone")
    val milestone: Milestone,
    @SerializedName("node_id")
    val nodeId: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("pull_request")
    val pullRequest: PullRequest,
    @SerializedName("repository_url")
    val repositoryUrl: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("state_reason")
    val stateReason: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("user")
    val user: User
)