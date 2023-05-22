package com.hasan.jetfasthub.screens.main.home.events.models

data class Payload(
    val action: String,
    val before: String,
    val commits: List<Commit>,
    val distinct_size: Int,
    val head: String,
    val push_id: Long,
    val ref: String,
    val size: Int
)