package com.hasan.jetfasthub.screens.main.gists.gist_comments_model

data class GistCommentsModelItem(
    val author_association: String,
    val body: String,
    val created_at: String,
    val id: Int,
    val node_id: String,
    val updated_at: String,
    val url: String,
    val user: User
)