package com.hasan.jetfasthub.screens.main.search.models.code_model

data class CodeItem(
    val git_url: String,
    val html_url: String,
    val name: String,
    val path: String,
    val repository: Repository,
    val score: Int,
    val sha: String,
    val url: String
)