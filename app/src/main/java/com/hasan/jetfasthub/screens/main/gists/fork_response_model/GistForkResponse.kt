package com.hasan.jetfasthub.screens.main.gists.fork_response_model

import com.hasan.jetfasthub.screens.main.gists.gist_model.GistFile

data class GistForkResponse(
    val comments: Int,
    val comments_url: String,
    val commits_url: String,
    val created_at: String,
    val description: String,
    val files: Map<String, GistFile>,
    val forks_url: String,
    val git_pull_url: String,
    val git_push_url: String,
    val html_url: String,
    val id: String,
    val node_id: String,
    val owner: Owner,
    val `public`: Boolean,
    val truncated: Boolean,
    val updated_at: String,
    val url: String,
    val user: Any
)