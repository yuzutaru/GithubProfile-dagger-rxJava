package com.yuzu.githubprofile.model.network.repository

import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.data.ProfileData
import com.yuzu.githubprofile.model.network.api.ProfileApi
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

class ProfileRepositoryImpl(private val api: ProfileApi): ProfileRepository {
    override fun userList(since: String): Single<List<UserData>> {
        return api.userList(since)
    }

    override fun userDetail(username: String): Single<ProfileData> {
        return api.userDetail(username)
    }
}