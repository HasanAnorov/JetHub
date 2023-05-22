package com.hasan.jetfasthub.screens.main.home.events.models

data class Commit(
    val author: Author,
    val distinct: Boolean,
    val message: String,
    val sha: String,
    val url: String
)