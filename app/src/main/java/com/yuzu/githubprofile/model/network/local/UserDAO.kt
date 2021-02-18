package com.yuzu.githubprofile.model.network.local

import androidx.room.*
import com.yuzu.githubprofile.model.data.UserData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

@Dao
interface UserDAO {
    @Query("SELECT * from UserData")
    fun getAllUsers(): Single<List<UserData>>

    @Query("SELECT * FROM UserData LIMIT 1")
    fun getUser(): Single<UserData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userDataList: List<UserData>)

    @Query("DELETE FROM UserData")
    fun deleteAllLogin()
}