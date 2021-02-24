package com.yuzu.githubprofile.model.network.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Yustar Pramudana on 22/02/2021
 */

class UserDataSourceFactory(private val profileRepository: ProfileRepository, private val compositeDisposable: CompositeDisposable): DataSource.Factory<Int, UserData>() {
    val userDataSourceLiveData = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, UserData> {
        val userDataSource = UserDataSource(profileRepository, compositeDisposable)
        userDataSourceLiveData.postValue(userDataSource)
        return userDataSource
    }
}