package com.hasan.jetfasthub.screens.main.gists.gist_model

data class History(
    val change_status: ChangeStatus,
    val committed_at: String,
    val url: String,
    val user: User,
    val version: String
)