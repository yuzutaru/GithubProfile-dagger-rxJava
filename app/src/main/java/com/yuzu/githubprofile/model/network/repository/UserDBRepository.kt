package com.yuzu.githubprofile.model.network.repository

import com.yuzu.githubprofile.model.data.UserData
import io.reactivex.Single

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

interface UserDBRepository {
    fun getAllUsers(since: Int): Single<List<UserData>>
    fun getUser(): Single<UserData>
    fun insert(userData: UserData)
    fun insert(userDataList: List<UserData>)
    fun deleteAllUsers()
    fun deleteAllAndInsert(list: List<UserData>)
}