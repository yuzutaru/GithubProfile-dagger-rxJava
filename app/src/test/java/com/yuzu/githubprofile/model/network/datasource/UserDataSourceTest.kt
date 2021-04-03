package com.yuzu.githubprofile.model.network.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PageKeyedDataSource
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.model.network.repository.UserDBRepository
import com.yuzu.githubprofile.utils.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer

/**
 * Created by Yustar Pramudana on 26/03/2021
 */

class UserDataSourceTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val profileRepo: ProfileRepository = mockk()

    private val userDBRepo: UserDBRepository = mockk()

    private val compositeDisposable = CompositeDisposable()

    @Before
    fun setup() {
        RxJavaPlugins.setErrorHandler {  }
    }

    @Test
    fun loadInitialTest() {
        val params = mockk<PageKeyedDataSource.LoadInitialParams<Int>>()
        val callBack = mockk<PageKeyedDataSource.LoadInitialCallback<Int, UserData>>()
        val userList = mockk<List<UserData>>()

        every { userDBRepo.getUserBySinceId(0) } returns Single.just(userList)

        val userDS = UserDataSource(profileRepo, userDBRepo, compositeDisposable)
        do
        doAnswer {

            val args: Array<Any> = it.arguments
            (args[0] as  PageKeyedDataSource.LoadInitialCallback<Int, UserData>).onResult(userList, null, userList[userList.size - 1].id)

            null
        }.`when`(userDS.loadInitial(params, callBack))

        // This version is not fired
        //verify { loadInitialCallback.onResult(any(), any(), any()) wasNot called }

        // This version works fine
        verify(exactly = 0) { callBack.onResult(userList, null, userList[userList.size - 1].id) }
    }

    @Test
    fun getState() {
    }

    @Test
    fun setState() {
    }

    @Test
    fun loadInitial() {
    }

    @Test
    fun loadAfter() {
    }

    @Test
    fun loadBefore() {
    }

    @Test
    fun retry() {
    }

    @Test
    fun updateUser() {
    }
}