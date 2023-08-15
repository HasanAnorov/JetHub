package com.hasan.jetfasthub.screens.main.home.data.local

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface HomeDao {

    @Upsert
    suspend fun upsertAll(events: List<ReceivedEventsModelEntity>)

    @Query("SELECT * FROM events_entity")
    fun pagingSource(): PagingSource<Int, ReceivedEventsModelEntity>

    @Query("DELETE FROM events_entity")
    suspend fun clearAll(){
        Log.d("ahi3646", "clearAll: ")
    }

}