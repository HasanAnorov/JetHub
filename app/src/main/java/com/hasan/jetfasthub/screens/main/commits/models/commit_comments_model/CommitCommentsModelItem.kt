package com.hasan.jetfasthub.screens.main.commits.models.commit_comments_model

data class CommitCommentsModelItem(
    val author_association: String,
    val body: String,
    val commit_id: String,
    val created_at: String,
    val html_url: String,
    val id: Int,
    val line: Int,
    val node_id: String,
    val path: String,
    val position: Int,
    val updated_at: String,
    val url: String,
    val user: User?
)