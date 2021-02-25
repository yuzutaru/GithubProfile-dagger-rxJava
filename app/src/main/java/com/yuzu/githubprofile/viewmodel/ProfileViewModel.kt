package com.yuzu.githubprofile.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.model.NoNetworkException
import com.yuzu.githubprofile.model.Response
import com.yuzu.githubprofile.model.Status
import com.yuzu.githubprofile.model.data.ConnectionLiveData
import com.yuzu.githubprofile.model.data.ConnectionModel
import com.yuzu.githubprofile.model.data.NotesData
import com.yuzu.githubprofile.model.data.ProfileData
import com.yuzu.githubprofile.model.network.repository.NotesDBRepository
import com.yuzu.githubprofile.model.network.repository.ProfileDBRepository
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.utils.ARGUMENT_LOGIN
import com.yuzu.githubprofile.utils.RETRY_DELAY
import com.yuzu.githubprofile.utils.RETRY_MAX
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
    private val notesDBRepository: NotesDBRepository

    private val profile = MutableLiveData<Response<ProfileData>>()
    fun profileDataLive(): LiveData<Response<ProfileData>> = profile

    private val profileDB = MutableLiveData<Response<ProfileData>>()
    fun profileDBDataLive(): LiveData<Response<ProfileData>> = profileDB

    private var login = ""
    var profileData = MutableLiveData<ProfileData>()
    var notesData = MutableLiveData<NotesData>()
    lateinit var connectionLiveData: ConnectionLiveData

    init {
        val appComponent = GithubProfileApplication.instance.getAppComponent()
        profileRepository = appComponent.profileRepository()
        profileDBRepository = appComponent.profileDBRepository()
        notesDBRepository = appComponent.notesDBRepository()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun saveNotes() {
        notesDBRepository.insert(NotesData(profileData.value!!.id, profileData.value!!.login, fragment.binding.notes.text.toString()))
        Toast.makeText(fragment.context, "Note has been saved", Toast.LENGTH_LONG).show()
    }

    private fun getNotes(login: String) {
        compositeDisposable.add(
                notesDBRepository.get(login)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { response ->
                                    if (response != null) {
                                        notesData.value = response
                                    }
                                }, {}
                        )
        )
    }

    fun getLogin(arguments: Bundle?) {
        if (arguments != null) {
            login = arguments.get(ARGUMENT_LOGIN).toString()
            profileDB(login)
        }
    }

    private fun profile(login: String?) {
        loading.value = true
        if (login != null) {
            compositeDisposable.add(
                profileRepository.userDetail(login)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(
                        RetryWithDelay(
                            RETRY_MAX,
                            RETRY_DELAY.toInt()
                        )
                    )
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
                    getNotes(profileData.value!!.login!!)
                    profileDBRepository.insert(profileData.value!!)

                    Glide.with(fragment).load(profileData.value!!.avatarUrl).diskCacheStrategy(DiskCacheStrategy.DATA).into(fragment.binding.avatar)
                    loading.value = false
                }

            } else if (response.status == Status.FAILED) {
                if (response.error != null) {
                    if (response.error.message != null) {
                        if (response.error.message!!.contains("Unable to resolve host", true)) {
                            Log.e(LOG_TAG, "errorMessage : ${fragment.resources.getString(R.string.no_connection)}")
                            Toast.makeText(fragment.context, fragment.resources.getString(R.string.no_connection), Toast.LENGTH_LONG).show()

                            loading.value = false

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
                            profile(login)
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
                    getNotes(profileData.value!!.login!!)

                    Glide.with(fragment).load(profileData.value!!.avatarUrl).diskCacheStrategy(DiskCacheStrategy.DATA).into(fragment.binding.avatar)
                    loading.value = false

                    //Update profile
                    updateProfile(login)

                } else {
                    profile(login)
                }

            } else if (response.status == Status.FAILED) {
                if (response.error != null) {
                    Log.e(LOG_TAG, "errorMessage : ${response.error.message}")
                    loading.value = false
                    Toast.makeText(fragment.context, fragment.resources.getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                }

            } else if (response.status == Status.NO_CONNECTION) {
                Log.e(LOG_TAG, "errorMessage : ${fragment.resources.getString(R.string.no_connection)}")
                Toast.makeText(fragment.context, fragment.resources.getString(R.string.no_connection), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e(LOG_TAG, it) }
        }
    }

    fun connection(connection: ConnectionModel?) {
        if (connection != null) {
            if (connection.isConnected) {
                when (connection.type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        Toast.makeText(fragment.context, String.format("Wifi turned ON"), Toast.LENGTH_SHORT).show()
                        profile(login)
                    }
                    ConnectivityManager.TYPE_MOBILE -> {
                        Toast.makeText(fragment.context, String.format("Mobile data turned ON"), Toast.LENGTH_SHORT).show()
                        profile(login)
                    }
                }
            } else {
                Toast.makeText(fragment.context, String.format("Connection turned OFF"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfile(login: String?) {
        loading.value = true
        if (login != null) {
            compositeDisposable.add(
                profileRepository.userDetail(login)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(
                        RetryWithDelay(
                            RETRY_MAX,
                            RETRY_DELAY.toInt()
                        )
                    )
                    .subscribe(
                        { res ->
                            profileDBRepository.insert(res)
                        },
                        {

                        }
                    )
            )
        }
    }
}