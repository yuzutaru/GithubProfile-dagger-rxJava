package com.yuzu.githubprofile.repository.model.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.repository.remote.contract.ProfileRepository
import com.yuzu.githubprofile.repository.model.contract.UserDBRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Yustar Pramudana on 22/02/2021
 */

class UserDataSourceFactory(private val profileRepository: ProfileRepository, private val userDBRepository: UserDBRepository, private val compositeDisposable: CompositeDisposable):
    DataSource.Factory<Int, UserData>() {
    val userDataSourceLiveData = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, UserData> {
        val userDataSource = UserDataSource(profileRepository, userDBRepository, compositeDisposable)
        userDataSourceLiveData.postValue(userDataSource)
        return userDataSource
    }
}