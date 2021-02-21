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
import com.yuzu.githubprofile.model.data.ProfileData
import com.yuzu.githubprofile.model.network.repository.ProfileDBRepository
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
    lateinit var fragment: ProfileFragment

    private val compositeDisposable = CompositeDisposable()
    private val profileRepository: ProfileRepository
    private val profileDBRepository: ProfileDBRepository

    private val profile = MutableLiveData<Response<ProfileData>>()
    fun profileDataLive(): LiveData<Response<ProfileData>> = profile

    private val profileDB = MutableLiveData<Response<ProfileData>>()
    fun profileDBDataLive(): LiveData<Response<ProfileData>> = profileDB

    private var login = ""
    var profileData = MutableLiveData<ProfileData>()

    init {
        val appComponent = GithubProfileApplication.instance.getAppComponent()
        profileRepository = appComponent.profileRepository()
        profileDBRepository = appComponent.profileDBRepository()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun getLogin(arguments: Bundle?) {
        if (arguments != null) {
            login = arguments.get(ARGUMENT_LOGIN).toString()
            profileDB(login)
        }
    }

    private fun profileDB(login: String?) {
        loading.value = true
        if (login != null) {
            compositeDisposable.add(
                profileDBRepository.getProfile(login)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                                res -> profileDB.value = Response.succeed(res)
                        },
                        {
                            profileDB.value = when (it) {
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

    fun profileDBRes(response: Response<ProfileData>) {
        try {
            Log.d(LOG_TAG, "DATA STATUS = ${response.status}")

            if (response.status == Status.SUCCEED) {
                if (response.data != null) {
                    profileData.value = response.data

                    Glide.with(fragment).load(profileData.value!!.avatarUrl).into(fragment.binding.avatar)
                    loading.value = false

                } else {
                    profile(login)
                }

            } else if (response.status == Status.FAILED) {
                if (response.error != null) {
                    Log.e(LOG_TAG, "errorMessage : ${response.error.message}")
                    profile(login)
                    //Toast.makeText(fragment.context, response.error.message, Toast.LENGTH_LONG).show()
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

    private fun profile(login: String?) {
        loading.value = true
        if (login != null) {
            compositeDisposable.add(
                    profileRepository.userDetail(login)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        res -> profile.value = Response.succeed(res)
                                    },
                                    {
                                        profile.value = when (it) {
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

    fun profileRes(response: Response<ProfileData>) {
        try {
            Log.d(LOG_TAG, "DATA STATUS = ${response.status}")

            if (response.status == Status.SUCCEED) {
                if (response.data != null) {
                    profileData.value = response.data
                    profileDBRepository.insert(profileData.value!!)

                    Glide.with(fragment).load(profileData.value!!.avatarUrl).into(fragment.binding.avatar)
                    loading.value = false
                }

            } else if (response.status == Status.FAILED) {
                if (response.error != null) {
                    if (response.error.message != null) {
                        if (response.error.message!!.contains("Unable to resolve host", true)) {
                            Log.e(LOG_TAG, "errorMessage : ${fragment.resources.getString(R.string.no_connection)}")
                            Toast.makeText(fragment.context, fragment.resources.getString(R.string.no_connection), Toast.LENGTH_LONG).show()

                        } else {
                            Log.e(LOG_TAG, "errorMessage : ${response.error.message}")
                            Toast.makeText(fragment.context, response.error.message, Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Log.e(LOG_TAG, "errorMessage : General Error")
                        Toast.makeText(fragment.context, "General Error", Toast.LENGTH_LONG).show()
                    }
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