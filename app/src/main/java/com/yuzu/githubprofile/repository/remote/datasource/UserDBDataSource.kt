package com.yuzu.githubprofile.repository.remote.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.data.State
import com.yuzu.githubprofile.repository.model.contract.UserDBRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

class UserDBDataSource(private val userDBRepository: UserDBRepository, private val compositeDisposable: CompositeDisposable, private val search: String):
        PageKeyedDataSource<Int, UserData>() {
    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserData>) {
        updateState(State.LOADING)
        compositeDisposable.add(
                userDBRepository.getUsersBySearch(search)
                        .subscribe(
                                { response ->
                                    Log.e("USerDS", "response = ${response.size}")
                                    if (!response.isNullOrEmpty()) {
                                        updateState(State.DONE)
                                        callback.onResult(response,
                                                null,
                                                0,
                                        )

                                    } else {

                                    }
                                },
                                {

                                }
                        )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserData>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserData>) {
        updateState(State.DONE)
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
}