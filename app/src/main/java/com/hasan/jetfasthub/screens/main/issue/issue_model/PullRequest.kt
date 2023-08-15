package com.hasan.jetfasthub.screens.main.issue.issue_model

data class PullRequest(
    val diff_url: String,
    val html_url: String,
    val patch_url: String,
    val url: String
)