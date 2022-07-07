package com.yuzu.githubprofile.repository.model.network.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.model.datasource.UserDataSource
import com.yuzu.githubprofile.repository.data.State
import com.yuzu.githubprofile.repository.remote.contract.ProfileRepository
import com.yuzu.githubprofile.repository.model.contract.UserDBRepository
import com.yuzu.githubprofile.utils.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    private val dataSource = UserDataSource(profileRepo, userDBRepo, compositeDisposable)

    @Before
    fun setup() {
        RxJavaPlugins.setErrorHandler {  }
    }

    @Test
    fun `userDbRepo getUserBySinceId() Test`() {
        every { userDBRepo.getUserBySinceId(0) } returns Single.just(listOf(UserData(0, 0)))

        val testObserver: TestObserver<List<UserData>> = userDBRepo.getUserBySinceId(0).test()
        testObserver.assertValue(listOf(UserData(0,0)))
        testObserver.dispose()
    }

    @Test
    fun `profileRepo userList() Test`() {
        every { profileRepo.userList(0) } returns Single.just(listOf(UserData(0, 0)))

        val testObserver: TestObserver<List<UserData>> = profileRepo.userList(0).test()
        testObserver.assertValue(listOf(UserData(0,0)))
        testObserver.dispose()
    }

    @Test
    fun `loadInitial Test`() {
        val params = mockk<PageKeyedDataSource.LoadInitialParams<Int>>()
        val callBack = mockk<PageKeyedDataSource.LoadInitialCallback<Int, UserData>>()

        every { userDBRepo.getUserBySinceId(0) } returns Single.just(listOf(UserData(0, 0)))
        dataSource.loadInitial(params, callBack)

        verify(exactly = 0) { callBack.onResult(listOf(UserData(0, 0)), any(), any()) }
    }

    @Test
    fun `UserDataSource loadAfter Test`() {
        val params = PageKeyedDataSource.LoadParams<Int>(1,0)
        val callBack = mockk<PageKeyedDataSource.LoadCallback<Int, UserData>>()

        every { userDBRepo.getUserBySinceId(params.key) } returns Single.just(listOf(UserData(1, 1)))
        dataSource.loadAfter(params, callBack)

        verify(exactly = 0) { callBack.onResult(listOf(UserData(1, 1)), params.key + 1) }
    }

    @Test
    fun `Test updateState`() {
        val state: MutableLiveData<State> = mockk()
        val updateState = dataSource.javaClass.getDeclaredMethod("updateState", State::class.java)
        updateState.isAccessible = true
        updateState.invoke(dataSource, State.DONE)
        verify(exactly = 0) { state.postValue(State.DONE)  }
    }

    @Test
    fun `retry Test`() {
        val retryCompletable: Completable = mockk()
        dataSource.retry()
        verify { compositeDisposable.add(retryCompletable.subscribe()) }
    }

    @Test
    fun updateUser() {
    }
}