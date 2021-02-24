package com.yuzu.githubprofile.model.network.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by Yustar Pramudana on 24/02/2021
 */

@Database(entities = [SinceDB::class], version = 1)
abstract class SinceDB: RoomDatabase() {
    abstract fun sinceDAO(): SinceDAO
}