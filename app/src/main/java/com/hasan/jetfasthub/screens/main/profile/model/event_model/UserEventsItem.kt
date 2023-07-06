package com.hasan.jetfasthub.screens.main.profile.model.event_model

data class UserEventsItem(
    val actor: Actor,
    val created_at: String,
    val id: String,
    val payload: Payload,
    val `public`: Boolean,
    val repo: Repo,
    val type: String
)