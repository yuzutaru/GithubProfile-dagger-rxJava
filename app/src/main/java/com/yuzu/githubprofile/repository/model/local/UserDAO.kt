package com.yuzu.githubprofile.repository.model.local

import androidx.room.*
import com.yuzu.githubprofile.repository.data.UserData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

@Dao
interface UserDAO {
    @Query("SELECT * from UserData WHERE sinceId = :since")
    fun getUserBySinceId(since: Int): Single<List<UserData>>

    @Query("SELECT * FROM UserData WHERE UserData.login like :search||'%' OR UserData.notes like :search||'%'")
    fun getUsersBySearch(search: String): Single<List<UserData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userDataList: List<UserData>)

    @Query("UPDATE UserData SET notes = :notes WHERE id = :id")
    fun updateNotes(id: Int, notes: String)
}