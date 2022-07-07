package com.yuzu.githubprofile.injection.module

import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.injection.AppModule
import com.yuzu.githubprofile.repository.remote.api.ProfileApi
import com.yuzu.githubprofile.repository.model.local.NotesDAO
import com.yuzu.githubprofile.repository.model.local.ProfileDAO
import com.yuzu.githubprofile.repository.model.local.UserDAO
import com.yuzu.githubprofile.repository.model.contract.NotesDBRepository
import com.yuzu.githubprofile.repository.model.contract.ProfileDBRepository
import com.yuzu.githubprofile.repository.remote.contract.ProfileRepository
import com.yuzu.githubprofile.repository.model.contract.UserDBRepository
import io.mockk.mockk
import java.util.concurrent.Executor

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

class TestApplicationModule(application: GithubProfileApplication): AppModule(application) {
    override fun profileApi(): ProfileApi = mockk()
    override fun profileRepository(api: ProfileApi): ProfileRepository = mockk()
    override fun profileDBRepository(dao: ProfileDAO, exec: Executor): ProfileDBRepository = mockk()
    override fun userDBRepository(dao: UserDAO, exec: Executor): UserDBRepository = mockk()
    override fun notesDBRepository(dao: NotesDAO, exec: Executor): NotesDBRepository = mockk()
}