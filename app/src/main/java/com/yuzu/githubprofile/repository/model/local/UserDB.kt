package com.yuzu.githubprofile.repository.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yuzu.githubprofile.repository.data.UserData

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

@Database(entities = [UserData::class], version = 1)
abstract class UserDB: RoomDatabase() {
    abstract fun userDAO(): UserDAO
}