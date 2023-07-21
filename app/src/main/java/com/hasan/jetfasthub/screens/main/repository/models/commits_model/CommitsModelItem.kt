package com.hasan.jetfasthub.screens.main.repository.models.commits_model

data class CommitsModelItem(
    val author: Author?,
    val comments_url: String,
    val commit: Commit,
    val committer: Committer,
    val html_url: String,
    val node_id: String,
    val parents: List<Parent>,
    val sha: String,
    val url: String
)