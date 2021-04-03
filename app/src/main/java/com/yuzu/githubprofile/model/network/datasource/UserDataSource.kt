package com.yuzu.githubprofile.model.network.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.State
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.model.network.repository.UserDBRepository
import com.yuzu.githubprofile.utils.RETRY_DELAY
import com.yuzu.githubprofile.utils.RETRY_MAX
import com.yuzu.githubprofile.viewmodel.RetryWithDelay
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

/**
 * Created by Yustar Pramudana on 22/02/2021
 */

class UserDataSource(private val profileRepository: ProfileRepository, private val userDBRepository: UserDBRepository, private val compositeDisposable: CompositeDisposable):
    PageKeyedDataSource<Int, UserData>() {
    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserData>) {
        updateState(State.LOADING)
        compositeDisposable.add(
            userDBRepository.getUserBySinceId(0)
                .subscribe(
                { response ->
                    if (!response.isNullOrEmpty()) {
                        updateState(State.DONE)
                        if (response.size > 1) {
                            callback.onResult(response,
                                null,
                                response[response.size - 1].id
                            )
                            updateUser(0)

                        }

                    } else {
                        userInitial(0, params, callback)
                    }
                },
                {
                    userInitial(0, params, callback)
                },
            )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserData>) {
        updateState(State.LOADING)
        compositeDisposable.add(
            userDBRepository.getUserBySinceId(params.key)
                .subscribe(
                    { response ->
                        if (!response.isNullOrEmpty()) {
                            if (response.size > 1) {
                                updateState(State.DONE)
                                callback.onResult(response, params.key + 1)
                                updateUser(params.key)
                            }

                        } else {
                            userAfter(params.key, params, callback)
                        }
                    },
                    {
                        userAfter(params.key, params, callback)
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserData>) {

    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
        }
    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }

    private fun userInitial(since: Int, params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserData>) {
        compositeDisposable.add(
            profileRepository.userList(since)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(
                    RetryWithDelay(
                        RETRY_MAX,
                        RETRY_DELAY.toInt()
                    )
                )
                .subscribe(
                    {   response ->
                        if (!response.isNullOrEmpty()) {
                            updateState(State.DONE)
                            for (i in response.indices) {
                                response[i].sinceId = 0
                            }
                            userDBRepository.insert(response)
                            callback.onResult(response,
                                null,
                                response[response.size - 1].id
                            )
                        } else {
                            updateState(State.ERROR)
                            setRetry(Action { loadInitial(params, callback) })
                        }

                    }, {
                        updateState(State.ERROR)
                        setRetry(Action { loadInitial(params, callback) })
                    }
                )
        )
    }

    private fun userAfter(since: Int, params: LoadParams<Int>, callback: LoadCallback<Int, UserData>) {
        compositeDisposable.add(
            profileRepository.userList(since)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(
                    RetryWithDelay(
                        RETRY_MAX,
                        RETRY_DELAY.toInt()
                    )
                )
                .subscribe(
                    {   response ->
                        if (!response.isNullOrEmpty()) {
                            updateState(State.DONE)
                            for (i in response.indices) {
                                response[i].sinceId = params.key
                            }
                            userDBRepository.insert(response)
                            callback.onResult(response, params.key + 1)
                        } else {
                            updateState(State.ERROR)
                            setRetry(Action {loadAfter(params, callback)})
                        }

                    }, {
                        updateState(State.ERROR)
                        setRetry(Action {loadAfter(params, callback)})
                    }
                )
        )
    }

    fun updateUser(since: Int) {
        compositeDisposable.add(
            profileRepository.userList(since)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (!response.isNullOrEmpty()) {
                            for (i in response.indices) {
                                response[i].sinceId = since
                            }
                            userDBRepository.insert(response)
                        }
                    },
                    {

                    }
                )
        )
    }
}