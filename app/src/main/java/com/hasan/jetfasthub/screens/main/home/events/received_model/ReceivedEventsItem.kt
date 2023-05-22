package com.hasan.jetfasthub.screens.main.home.events.received_model

data class ReceivedEventsItem(
    val actor: Actor,
    val created_at: String,
    val id: String,
    val org: Org,
    val payload: Payload,
    val `public`: Boolean,
    val repo: Repo,
    val type: String
)