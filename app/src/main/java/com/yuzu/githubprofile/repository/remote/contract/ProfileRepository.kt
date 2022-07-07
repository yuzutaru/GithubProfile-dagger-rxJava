package com.yuzu.githubprofile.repository.remote.contract

import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.data.ProfileData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

interface ProfileRepository {
    fun userList(since: Int): Single<List<UserData>>
    fun userDetail(username: String): Single<ProfileData>
}