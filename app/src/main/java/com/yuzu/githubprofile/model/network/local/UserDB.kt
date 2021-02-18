package com.yuzu.githubprofile.model.network.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yuzu.githubprofile.model.data.UserData

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

@Database(entities = [UserData::class], version = 1)
abstract class UserDB: RoomDatabase() {
    abstract fun userDataDAO(): UserDAO
}