package com.yuzu.githubprofile.injection.component

import android.app.Application
import com.yuzu.githubprofile.injection.module.AppModule
import com.yuzu.githubprofile.model.network.api.ProfileApi
import com.yuzu.githubprofile.model.network.local.UserDAO
import com.yuzu.githubprofile.model.network.local.UserDB
import com.yuzu.githubprofile.model.network.repository.UserDBRepository
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import dagger.Component

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: Application)

    //Profile API
    fun profileRepository(): ProfileRepository
    fun profileApi(): ProfileApi

    //Profile ROOM DATA
    fun userDBRepository(): UserDBRepository
    fun userDB(): UserDB
    fun userDAO(): UserDAO
}