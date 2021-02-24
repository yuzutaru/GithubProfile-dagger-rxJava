package com.yuzu.githubprofile.model.network.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yuzu.githubprofile.model.data.SinceData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 24/02/2021
 */

interface SinceDBRepository {
    fun getAllSince(): Single<List<SinceData>>
    fun getSince(since: Int): Single<SinceData>
    fun insert(data: SinceData)
    fun insert(list: List<SinceData>)
    fun deleteAllSinces()
    fun deleteAllAndInsert(list: List<SinceData>)
}