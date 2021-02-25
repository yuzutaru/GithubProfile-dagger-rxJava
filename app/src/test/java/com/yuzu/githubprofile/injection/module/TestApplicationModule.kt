package com.yuzu.githubprofile.injection.module

import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.model.network.api.ProfileApi
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import io.mockk.mockk
import okhttp3.OkHttpClient

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

class TestApplicationModule(application: GithubProfileApplication): AppModule(application) {
    override fun provideOkHttpClient(): OkHttpClient = mockk()

    override fun profileApi(): ProfileApi = mockk()
    override fun profileRepository(api: ProfileApi): ProfileRepository = mockk()
}