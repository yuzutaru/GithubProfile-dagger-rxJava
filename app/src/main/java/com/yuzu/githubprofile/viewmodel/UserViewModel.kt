package com.yuzu.githubprofile.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.model.NoNetworkException
import com.yuzu.githubprofile.model.Response
import com.yuzu.githubprofile.model.Status
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.view.fragment.UserFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

class UserViewModel(app: Application): AndroidViewModel(app) {
    private val LOG_TAG = "User"

    private val compositeDisposable = CompositeDisposable()
    private val profileRepository: ProfileRepository

    private val user = MutableLiveData<Response<List<UserData>>>()
    fun userDataLive(): LiveData<Response<List<UserData>>> = user

    lateinit var userList: List<UserData>

    init {
        val appComponent = GithubProfileApplication.instance.getAppComponent()
        profileRepository = appComponent.profileRepository()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun getUser() {
        compositeDisposable.add(
            profileRepository.userList("0")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {  }
                .subscribe(
                    {
                        res -> user.value = Response.succeed(res)
                    },
                    {
                        user.value = when(it) {
                            is NoNetworkException -> {
                                Response.networkLost()
                            }
                            else -> Response.error(it)
                        }
                    }
                )
        )
    }

    fun userResponse(fragment: UserFragment, response: Response<List<UserData>>) {
        try {
            Log.d(LOG_TAG, "DATA STATUS = ${response.status}")

            if (response.status == Status.SUCCEED) {
                if (response.data != null) {
                    userList = response.data
                    fragment.userSuccess()
                }

            } else if (response.status == Status.FAILED) {
                if (response.error != null) {
                    Log.e(LOG_TAG, "errorMessage : ${response.error.message}")
                    Toast.makeText(fragment.context, response.error.message, Toast.LENGTH_LONG).show()
                }

            } else if (response.status == Status.NO_CONNECTION) {
                Log.e(LOG_TAG, "errorMessage : ${fragment.resources.getString(R.string.no_connection)}")
                Toast.makeText(fragment.context, fragment.resources.getString(R.string.no_connection), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e(LOG_TAG, it) }
        }
    }
}
