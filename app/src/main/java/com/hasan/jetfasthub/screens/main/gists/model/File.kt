package com.hasan.jetfasthub.screens.main.gists.model

data class File(
    val filename: String,
    val language: String,
    val raw_url: String,
    val size: Int,
    val type: String
)