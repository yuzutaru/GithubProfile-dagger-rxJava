package com.yuzu.githubprofile.repository.model.contract

import com.yuzu.githubprofile.repository.data.ProfileData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 21/02/2021
 */

interface ProfileDBRepository {
    fun getAllProfiles(): Single<List<ProfileData>>
    fun getProfile(login: String): Single<ProfileData>
    fun insert(profileData: ProfileData)
    fun insert(profileDataList: List<ProfileData>)
}