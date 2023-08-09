package com.hasan.jetfasthub.screens.main.commits.models.commit_model

data class CommitModel(
    val author: Author?,
    val comments_url: String,
    val commit: Commit,
    val committer: CommitterX,
    val files: List<File>,
    val html_url: String,
    val node_id: String,
    val parents: List<Parent>,
    val sha: String,
    val stats: Stats,
    val url: String
)