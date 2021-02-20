package com.yuzu.githubprofile.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.databinding.ItemUserListBinding
import com.yuzu.githubprofile.model.NoNetworkException
import com.yuzu.githubprofile.model.Response
import com.yuzu.githubprofile.model.Status
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.view.fragment.UserFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

class UserViewModel(app: Application): AndroidViewModel(app) {
    private val LOG_TAG = "User"
    var loading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val compositeDisposable = CompositeDisposable()
    private val profileRepository: ProfileRepository

    private val user = MutableLiveData<Response<List<UserData>>>()
    fun userDataLive(): LiveData<Response<List<UserData>>> = user

    val login = MutableLiveData<String>()
    fun loginDataLive(): LiveData<String> = login

    lateinit var userList: List<UserData>
    private var itemClicked = false

    init {
        val appComponent = GithubProfileApplication.instance.getAppComponent()
        profileRepository = appComponent.profileRepository()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun setImage(i: Int, data: List<UserData>, itemView: View, binding: ItemUserListBinding) {
        if ((i + 1).rem(4) == 0 && i > 0 && (i + 1) < data.size) {
            doAsync {
                var bitmap = getBitmapFromURL(data[i].avatarUrl)
                bitmap = invertBitmap(bitmap!!)
                uiThread { Glide.with(itemView).load(bitmap).into(binding.avatar) }
            }.isDone

        } else {
            Glide.with(itemView).load(data[i].avatarUrl).into(binding.avatar)
        }
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

    private fun invertBitmap(bitmap: Bitmap): Bitmap {
        val length = bitmap.width * bitmap.height
        val array = IntArray(length)
        val secondaryBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        secondaryBitmap.getPixels(array, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in 0 until length) {
            if (array[i] == 0xff000000.toInt()) {
                array[i] = 0xffffffff.toInt()
            }
        }

        secondaryBitmap.setPixels(array, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        return secondaryBitmap
    }

    fun itemClicked(data: String?) {
        if (data != null) {
            itemClicked = true
            login.value = data
        }

    }

    fun itemClicked(fragment: UserFragment, response: String) {
        fragment.userDetail(response)
        itemClicked = false
    }

    fun getUser() {
        loading.value = true
        compositeDisposable.add(
            profileRepository.userList("0")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { res ->
                        user.value = Response.succeed(res)
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
