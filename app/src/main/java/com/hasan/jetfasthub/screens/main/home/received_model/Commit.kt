package com.hasan.jetfasthub.screens.main.home.received_model

data class Commit(
    val author: Author,
    val distinct: Boolean,
    val message: String,
    val sha: String,
    val url: String
)