package com.hasan.jetfasthub.screens.main.home.events.models

data class EventItem(
    val actor: Actor,
    val created_at: String,
    val id: String,
    val payload: Payload,
    val `public`: Boolean,
    val repo: Repo,
    val type: String
)