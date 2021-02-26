package com.yuzu.githubprofile.model.network.repository

import com.yuzu.githubprofile.model.data.UserData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

interface UserDBRepository {
    fun getUserBySinceId(since: Int): Single<List<UserData>>
    fun getUsersBySearch(search: String): Single<List<UserData>>
    fun insert(userData: UserData)
    fun insert(userDataList: List<UserData>)
}