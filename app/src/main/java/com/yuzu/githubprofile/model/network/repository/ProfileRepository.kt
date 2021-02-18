package com.yuzu.githubprofile.model.network.repository

import android.service.autofill.UserData
import com.yuzu.githubprofile.model.data.UserDetail
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

interface ProfileRepository {
    fun userList(since: String): Single<List<UserData>>
    fun userDetail(username: String): Single<UserDetail>
}