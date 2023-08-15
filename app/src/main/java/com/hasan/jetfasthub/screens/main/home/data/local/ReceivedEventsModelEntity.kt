package com.hasan.jetfasthub.screens.main.home.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("events_entity")
data class ReceivedEventsModelEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val eventType: String,
    val eventRepoUrl: String,
    val eventRepoName: String,
    val eventActorLogin: String,
    val eventActorAvatarUrl: String,
    val eventCreatedAt: String,
    val eventPayloadForkeeName: String
)