package com.hasan.jetfasthub.screens.main.issue.comment_response_model

data class CommentResponseModel(
    val author_association: String,
    val body: String,
    val created_at: String,
    val html_url: String,
    val id: Int,
    val issue_url: String,
    val node_id: String,
    val updated_at: String,
    val url: String,
    val user: User
)