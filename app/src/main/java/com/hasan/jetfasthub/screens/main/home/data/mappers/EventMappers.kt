package com.hasan.jetfasthub.screens.main.home.data.mappers

import com.hasan.jetfasthub.screens.main.home.data.local.ReceivedEventsModelEntity
import com.hasan.jetfasthub.screens.main.home.data.remote.received_events_model_dto.ReceivedEventModelDto
import com.hasan.jetfasthub.screens.main.home.domain.ReceivedEventsModel

fun ReceivedEventModelDto.toReceivedEventsModelEntity():ReceivedEventsModelEntity{
    return ReceivedEventsModelEntity(
        id = 0,
        eventType = type,
        eventRepoUrl = repo.url,
        eventRepoName = repo.name,
        eventActorAvatarUrl = actor.avatar_url,
        eventActorLogin = actor.login,
        eventCreatedAt = created_at,
        eventPayloadForkeeName = payload.forkee?.name ?: ""
    )
}

fun ReceivedEventsModelEntity.toReceivedEventsModel(): ReceivedEventsModel{
    return ReceivedEventsModel(
        id = id,
        eventType = eventType,
        eventRepoUrl = eventRepoUrl,
        eventRepoName = eventRepoName,
        eventActorLogin = eventActorLogin,
        eventActorAvatarUrl = eventActorAvatarUrl,
        eventCreatedAt = eventCreatedAt,
        eventPayloadForkeeName = eventPayloadForkeeName
    )
}