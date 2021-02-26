package com.yuzu.githubprofile.model.network.api

import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.injection.component.DaggerTestApplicationComponent
import com.yuzu.githubprofile.injection.module.TestApplicationModule
import com.yuzu.githubprofile.model.data.ProfileData
import com.yuzu.githubprofile.model.data.UserData
import io.mockk.every
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

class AppRepositoryInjectTest{
    @Inject
    lateinit var api: ProfileApi

    @Before
    fun setUp() {
        val component = DaggerTestApplicationComponent.builder()
            .appModule(TestApplicationModule(GithubProfileApplication()))
            .build()
        component.into(this)
    }

    @Test
    fun apiUserListTest() {
        Assert.assertNotNull(api)
        every { api.userList(0) } returns Single.just(listOf(UserData(0, 0)))
        val result = api.userList(0)
        result.test().assertValue(listOf(UserData(0, 0)))
    }

    @Test
    fun apiUserDetailTest() {
        Assert.assertNotNull(api)
        every { api.userDetail("name") } returns Single.just(ProfileData(0))
        val result = api.userDetail("name")
        result.test().assertValue(ProfileData(0))
    }
}