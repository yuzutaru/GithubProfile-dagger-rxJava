package com.yuzu.githubprofile.repository.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yuzu.githubprofile.repository.data.ProfileData

/**
 * Created by Yustar Pramudana on 21/02/2021
 */

@Database(entities = [ProfileData::class], version = 1)
abstract class ProfileDB: RoomDatabase() {
    abstract fun profileDAO(): ProfileDAO
}