package com.yuzu.githubprofile.model.network.local

import androidx.room.*
import com.yuzu.githubprofile.model.data.UserData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 24/02/2021
 */

@Dao
interface SinceDAO {
    @Query("SELECT * from SinceData LIMIT :itemSize")
    fun getAllUsers(itemSize: Int): Single<List<UserData>>

    @Query("SELECT * FROM UserData LIMIT 1")
    fun getUser(): Single<UserData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userDataList: List<UserData>)

    @Query("DELETE FROM UserData")
    fun deleteAllUsers()

    @Transaction
    fun deleteAllAndInsertsList(list: List<UserData>) {
        deleteAllUsers()
        insert(list)
    }
}