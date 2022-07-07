package com.yuzu.githubprofile.repository.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yuzu.githubprofile.repository.data.NotesData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

@Dao
interface NotesDAO {
    @Query("SELECT * from NotesData")
    fun getAll(): Single<List<NotesData>>

    @Query("SELECT * FROM NotesData WHERE login = :login")
    fun get(login: String): Single<NotesData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: NotesData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<NotesData>)
}