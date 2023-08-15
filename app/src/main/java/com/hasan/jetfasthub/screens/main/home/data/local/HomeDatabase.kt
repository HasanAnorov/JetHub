package com.hasan.jetfasthub.screens.main.home.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReceivedEventsModelEntity::class],
    version = 1
)
abstract class HomeDatabase: RoomDatabase() {

    abstract val dao: HomeDao

}