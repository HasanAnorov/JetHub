package com.hasan.jetfasthub.screens.main.home.events.received_model

data class Payload(
    val before: String,
    val commits: List<Commit>,
    val description: Any,
    val distinct_size: Int,
    val head: String,
    val master_branch: String,
    val push_id: Long,
    val pusher_type: String,
    val ref: String,
    val ref_type: String,
    val size: Int
)