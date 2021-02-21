package com.yuzu.githubprofile.model.network.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yuzu.githubprofile.model.data.ProfileData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 21/02/2021
 */

@Dao
interface ProfileDAO {
    @Query("SELECT * from ProfileData")
    fun getAllProfiles(): Single<List<ProfileData>>

    @Query("SELECT * FROM ProfileData LIMIT 1")
    fun getProfile(): Single<ProfileData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profileData: ProfileData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profileDataList: List<ProfileData>)

    @Query("DELETE FROM ProfileData")
    fun deleteAllProfiles()
}