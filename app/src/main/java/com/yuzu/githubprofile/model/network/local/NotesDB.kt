package com.yuzu.githubprofile.model.network.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yuzu.githubprofile.model.data.NotesData

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

@Database(entities = [NotesData::class], version = 1)
abstract class NotesDB: RoomDatabase() {
    abstract fun notesDAO(): NotesDAO
}
