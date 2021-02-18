package com.yuzu.githubprofile.model.network.api

import android.service.autofill.UserData
import com.yuzu.githubprofile.model.data.UserDetail
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
    fun userList(@Query("since") since: String): Single<List<UserData>>

    /**
     * User Detail
     * */
    @GET(value = "users/{username}")
    fun userDetail(@Path(value = "username") username: String): Single<UserDetail>
}