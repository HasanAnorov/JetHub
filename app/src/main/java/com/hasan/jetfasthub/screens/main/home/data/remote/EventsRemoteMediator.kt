package com.hasan.jetfasthub.screens.main.home.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hasan.jetfasthub.data.HomeRepository
import com.hasan.jetfasthub.screens.main.home.data.local.HomeDatabase
import com.hasan.jetfasthub.screens.main.home.data.local.ReceivedEventsModelEntity
import com.hasan.jetfasthub.screens.main.home.data.mappers.toReceivedEventsModelEntity
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class EventsRemoteMediator(
    private val repository: HomeRepository,
    private val homeDatabase: HomeDatabase,
    private val token: String,
    private val username: String
) : RemoteMediator<Int, ReceivedEventsModelEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ReceivedEventsModelEntity>
    ): MediatorResult {
        Log.d("ahi3646", "load: loadType - $loadType ")
        return try {
            val loadKey = when (loadType) {

                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        Log.d("ahi3646", "load: itemId - ${lastItem.id} - ${state.config.pageSize}")
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }
            Log.d("ahi3646", "load events mediator: $token - $username - id-${state.lastItemOrNull()?.id}")
            val events = repository.getReceivedUserEvents(
                token,
                username,
                loadKey,
                state.config.pageSize
            )

            homeDatabase.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    homeDatabase.dao.clearAll()
//                    homeDatabase.clearAllTables()
                }
                val eventEntities = events.map { it.toReceivedEventsModelEntity() }
                homeDatabase.dao.upsertAll(eventEntities)

            }
            MediatorResult.Success(
                endOfPaginationReached = events.isEmpty()
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

}