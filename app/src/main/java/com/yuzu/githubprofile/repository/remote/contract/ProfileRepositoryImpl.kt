package com.yuzu.githubprofile.repository.remote.contract

import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.data.ProfileData
import com.yuzu.githubprofile.repository.remote.api.ProfileApi
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

class ProfileRepositoryImpl(private val api: ProfileApi): ProfileRepository {
    override fun userList(since: Int): Single<List<UserData>> {
        return api.userList(since)
    }

    override fun userDetail(username: String): Single<ProfileData> {
        return api.userDetail(username)
    }
}