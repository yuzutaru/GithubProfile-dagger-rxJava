package com.yuzu.githubprofile.repository.remote.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.model.contract.UserDBRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

class UserDBDataSourceFactory(private val userDBRepository: UserDBRepository, private val compositeDisposable: CompositeDisposable, private val search: String):
        DataSource.Factory<Int, UserData>() {
    val userDBLiveData = MutableLiveData<UserDBDataSource>()

    override fun create(): DataSource<Int, UserData> {
        val userDBDataSource = UserDBDataSource(userDBRepository, compositeDisposable, search)
        userDBLiveData.postValue(userDBDataSource)
        return userDBDataSource
    }
}