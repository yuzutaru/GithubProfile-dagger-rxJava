package com.yuzu.githubprofile.model.network.local

import androidx.room.*
import com.yuzu.githubprofile.model.data.SinceData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 24/02/2021
 */

@Dao
interface SinceDAO {
    @Query("SELECT * from SinceData")
    fun getAllSince(): Single<List<SinceData>>

    @Query("SELECT * FROM UserData WHERE id = :since LIMIT 1")
    fun getSince(since: Int): Single<SinceData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: SinceData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<SinceData>)

    @Query("DELETE FROM SinceData")
    fun deleteAllSinces()

    @Transaction
    fun deleteAllAndInsert(list: List<SinceData>) {
        deleteAllSinces()
        insert(list)
    }
}