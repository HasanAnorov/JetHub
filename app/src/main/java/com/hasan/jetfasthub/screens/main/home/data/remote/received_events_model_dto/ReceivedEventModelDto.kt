package com.hasan.jetfasthub.screens.main.home.data.remote.received_events_model_dto

data class ReceivedEventModelDto(
    val actor: Actor,
    val created_at: String,
    val id: String,
    val org: Org,
    val payload: Payload,
    val `public`: Boolean,
    val repo: Repo,
    val type: String
)