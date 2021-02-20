package com.yuzu.githubprofile.viewmodel

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.model.NoNetworkException
import com.yuzu.githubprofile.model.Response
import com.yuzu.githubprofile.model.Status
import com.yuzu.githubprofile.model.data.Profile
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.utils.ARGUMENT_LOGIN
import com.yuzu.githubprofile.view.fragment.ProfileFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Yustar Pramudana on 20/02/2021
 */

class ProfileViewModel(app: Application): AndroidViewModel(app) {
    private val LOG_TAG = "UserDetail"
    var loading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val compositeDisposable = CompositeDisposable()
    private val profileRepository: ProfileRepository

    private val user = MutableLiveData<Response<Profile>>()
    fun userDataLive(): LiveData<Response<Profile>> = user

    var userDetail = MutableLiveData<Profile>()

    init {
        val appComponent = GithubProfileApplication.instance.getAppComponent()
        profileRepository = appComponent.profileRepository()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun getLogin(arguments: Bundle?) {
        if (arguments != null) {
            val login = arguments.getString(ARGUMENT_LOGIN)
            getUserDetail(login)
        }
    }

    private fun getUserDetail(login: String?) {
        loading.value = true

        if (login != null) {
            compositeDisposable.add(
                    profileRepository.userDetail(login)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        res -> user.value = Response.succeed(res)
                                    },
                                    {
                                        user.value = when (it) {
                                            is NoNetworkException -> {
                                                Response.networkLost()
                                            }
                                            else -> Response.error(it)
                                        }
                                    }
                            )
            )
        }
    }

    fun userDetail(fragment: ProfileFragment, response: Response<Profile>) {
        try {
            Log.d(LOG_TAG, "DATA STATUS = ${response.status}")

            if (response.status == Status.SUCCEED) {
                if (response.data != null) {
                    userDetail.value = response.data

                    if (userDetail != null) {
                        Glide.with(fragment).load(userDetail.value!!.avatarUrl).into(fragment.binding.avatar)
                    }
                    loading.value = false
                }

            } else if (response.status == Status.FAILED) {
                if (response.error != null) {
                    Log.e(LOG_TAG, "errorMessage : ${response.error.message}")
                    Toast.makeText(fragment.context, response.error.message, Toast.LENGTH_LONG).show()
                }

            } else if (response.status == Status.NO_CONNECTION) {
                Log.e(
                        LOG_TAG,
                        "errorMessage : ${fragment.resources.getString(R.string.no_connection)}"
                )
                Toast.makeText(
                        fragment.context,
                        fragment.resources.getString(R.string.no_connection),
                        Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e(LOG_TAG, it) }
        }
    }
}