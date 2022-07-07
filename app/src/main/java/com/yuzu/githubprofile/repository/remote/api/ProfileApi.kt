package com.yuzu.githubprofile.repository.remote.api

import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.data.ProfileData
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

interface ProfileApi {

    /**
     * User List
     * */
    @GET(value = "users")
    open fun userList(@Query("since") since: Int): Single<List<UserData>>

    /**
     * User Detail
     * */
    @GET(value = "users/{username}")
    open fun userDetail(@Path(value = "username") username: String): Single<ProfileData>
}