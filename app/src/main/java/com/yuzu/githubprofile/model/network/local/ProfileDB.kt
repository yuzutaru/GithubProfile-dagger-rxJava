package com.yuzu.githubprofile.model.network.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yuzu.githubprofile.model.data.ProfileData

/**
 * Created by Yustar Pramudana on 21/02/2021
 */

@Database(entities = [ProfileData::class], version = 1)
abstract class ProfileDB: RoomDatabase() {
    abstract fun profileDAO(): ProfileDAO
}