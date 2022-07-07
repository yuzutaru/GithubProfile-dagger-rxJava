package com.yuzu.githubprofile.injection

import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.injection.component.DaggerTestApplicationComponent
import com.yuzu.githubprofile.injection.module.TestApplicationModule
import com.yuzu.githubprofile.repository.data.NotesData
import com.yuzu.githubprofile.repository.data.ProfileData
import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.remote.api.ProfileApi
import com.yuzu.githubprofile.repository.model.contract.NotesDBRepository
import com.yuzu.githubprofile.repository.model.contract.ProfileDBRepository
import com.yuzu.githubprofile.repository.remote.contract.ProfileRepository
import com.yuzu.githubprofile.repository.model.contract.UserDBRepository
import io.mockk.every
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

/**
 * Created by Yustar Pramud54ana on 25/02/2021
 */

class AppRepositoryInjectTest {
    @Inject
    lateinit var api: ProfileApi

    @Inject
    lateinit var profileRepo: ProfileRepository

    @Inject
    lateinit var profileDBRepo: ProfileDBRepository

    @Inject
    lateinit var userDBRepo: UserDBRepository

    @Inject
    lateinit var notesDBRepo: NotesDBRepository

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

    @Test
    fun profileRepoUserListTest() {
        Assert.assertNotNull(profileRepo)
        every { profileRepo.userList(0) } returns Single.just(listOf(UserData(0, 0)))
        val result = profileRepo.userList(0)
        result.test().assertValue(listOf(UserData(0, 0)))
    }

    @Test
    fun profileRepoUserDetailTest() {
        Assert.assertNotNull(profileRepo)
        every { profileRepo.userDetail("yuzutaru") } returns Single.just(ProfileData(0, "yuzutaru"))
        val result = profileRepo.userDetail("yuzutaru")
        result.test().assertValue(ProfileData(0, "yuzutaru"))
    }

    @Test
    fun profileDBRepoGetAllProfilesTest() {
        Assert.assertNotNull(profileDBRepo)
        every { profileDBRepo.getAllProfiles() } returns Single.just(listOf(ProfileData(0)))
        val result = profileDBRepo.getAllProfiles()
        result.test().assertValue(listOf(ProfileData(0)))
    }

    @Test
    fun profileDBRepoGetProfileTest() {
        Assert.assertNotNull(profileDBRepo)
        every { profileDBRepo.getProfile("yuzutaru") } returns Single.just(ProfileData(0, "yuzutaru"))
        val result = profileDBRepo.getProfile("yuzutaru")
        result.test().assertValue(ProfileData(0, "yuzutaru"))
    }

    @Test
    fun userDBRepoGetUserBySinceIdTest() {
        Assert.assertNotNull(userDBRepo)
        every { userDBRepo.getUserBySinceId(0) } returns Single.just(listOf(UserData(0, 0)))
        val result = userDBRepo.getUserBySinceId(0)
        result.test().assertValue(listOf(UserData(0, 0)))
    }

    @Test
    fun userDBRepoGetUserBySearchTest() {
        Assert.assertNotNull(userDBRepo)
        every { userDBRepo.getUsersBySearch("yuz") } returns Single.just(listOf(UserData(0, 0, "yuzutaru")))
        val result = userDBRepo.getUsersBySearch("yuz")
        result.test().assertValue(listOf(UserData(0, 0, "yuzutaru")))
    }

    @Test
    fun notesDBRepoGetAllTest() {
        Assert.assertNotNull(notesDBRepo)
        every { notesDBRepo.getAll() } returns Single.just(listOf(NotesData(0)))
        val result = notesDBRepo.getAll()
        result.test().assertValue(listOf(NotesData(0)))
    }

    @Test
    fun notesDBRepoGetTest() {
        Assert.assertNotNull(notesDBRepo)
        every { notesDBRepo.get("yuzutaru") } returns Single.just(NotesData(0, "yuzutaru"))
        val result = notesDBRepo.get("yuzutaru")
        result.test().assertValue(NotesData(0, "yuzutaru"))
    }
}