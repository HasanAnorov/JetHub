package com.hasan.jetfasthub.screens.main.commits.models.commit_model

data class File(
    val additions: Int,
    val blob_url: String,
    val changes: Int,
    val deletions: Int,
    val filename: String,
    val patch: String,
    val raw_url: String,
    val status: String
)