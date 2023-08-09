package com.hasan.jetfasthub.screens.main.repository.models.branch_model

data class Commit(
    val author: Author,
    val comments_url: String,
    val commit: CommitX,
    val committer: CommitterX,
    val html_url: String,
    val node_id: String,
    val parents: List<Parent>,
    val sha: String,
    val url: String
)