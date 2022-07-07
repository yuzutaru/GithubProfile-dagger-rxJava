package com.yuzu.githubprofile.injection

import android.app.Application
import com.yuzu.githubprofile.repository.remote.api.ProfileApi
import com.yuzu.githubprofile.repository.model.local.*
import com.yuzu.githubprofile.repository.model.contract.NotesDBRepository
import com.yuzu.githubprofile.repository.model.contract.ProfileDBRepository
import com.yuzu.githubprofile.repository.remote.contract.ProfileRepository
import com.yuzu.githubprofile.repository.model.contract.UserDBRepository
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: Application)

    //Profile API
    fun profileApi(): ProfileApi
    fun profileRepository(): ProfileRepository

    //User ROOM DATA
    fun userDBRepository(): UserDBRepository
    fun userDB(): UserDB
    fun userDAO(): UserDAO

    //Profile ROOM DATA
    fun profileDBRepository(): ProfileDBRepository
    fun profileDB(): ProfileDB
    fun profileDAO(): ProfileDAO

    //Notes ROOM DATA
    fun notesDBRepository(): NotesDBRepository
    fun notesDB(): NotesDB
    fun notesDAO(): NotesDAO
}