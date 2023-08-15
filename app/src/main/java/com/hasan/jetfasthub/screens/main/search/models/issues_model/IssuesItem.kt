package com.hasan.jetfasthub.screens.main.search.models.issues_model

data class IssuesItem(
    val assignee: Any,
    val author_association: String,
    val body: String,
    val closed_at: Any?,
    val comments: Int,
    val comments_url: String,
    val created_at: String,
    val events_url: String,
    val html_url: String,
    val id: Int,
    val labels: List<Label>,
    val labels_url: String,
    val locked: Boolean,
    val milestone: Milestone,
    val node_id: String,
    val number: Int,
    val pull_request: PullRequest,
    val repository_url: String,
    val score: Int,
    val state: String,
    val state_reason: String,
    val title: String,
    val updated_at: String,
    val url: String,
    val user: User
)