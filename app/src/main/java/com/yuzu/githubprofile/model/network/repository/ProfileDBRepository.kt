package com.yuzu.githubprofile.model.network.repository

import com.yuzu.githubprofile.model.data.ProfileData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 21/02/2021
 */

interface ProfileDBRepository {
    fun getAllProfiles(): Single<List<ProfileData>>
    fun getProfile(login: String): Single<ProfileData>
    fun insert(profileData: ProfileData)
    fun insert(profileDataList: List<ProfileData>)
    fun deleteAllProfiles()
}