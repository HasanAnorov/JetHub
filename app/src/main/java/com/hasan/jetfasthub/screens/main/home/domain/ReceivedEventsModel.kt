package com.hasan.jetfasthub.screens.main.home.domain

class ReceivedEventsModel (
    val id: Int,
    val eventType: String,
    val eventRepoUrl: String,
    val eventRepoName: String,
    val eventActorLogin: String,
    val eventActorAvatarUrl: String,
    val eventCreatedAt: String,
    val eventPayloadForkeeName: String
)