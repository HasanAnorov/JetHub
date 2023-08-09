package com.hasan.jetfasthub.screens.main.gists.gist_model

data class GistFile(
    val content: String,
    val filename: String,
    val language: String,
    val raw_url: String,
    val size: Int,
    val truncated: Boolean,
    val type: String
)