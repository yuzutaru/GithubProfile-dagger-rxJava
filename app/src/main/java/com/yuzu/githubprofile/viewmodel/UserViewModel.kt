package com.yuzu.githubprofile.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.bumptech.glide.Glide
import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.databinding.FragmentUserBinding
import com.yuzu.githubprofile.databinding.ItemUserListBinding
import com.yuzu.githubprofile.model.Response
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.State
import com.yuzu.githubprofile.model.network.datasource.UserDataSource
import com.yuzu.githubprofile.model.network.datasource.UserDataSourceFactory
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.model.network.repository.UserDBRepository
import com.yuzu.githubprofile.view.adapter.UserListPagedAdapter
import com.yuzu.githubprofile.view.fragment.UserFragment
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

class UserViewModel(app: Application): AndroidViewModel(app) {
    private val LOG_TAG = "User"
    var loading: MutableLiveData<Boolean> = MutableLiveData(false)
    lateinit var fragment: UserFragment

    private val compositeDisposable = CompositeDisposable()
    private val profileRepository: ProfileRepository
    private val userDBRepository: UserDBRepository

    private val user = MutableLiveData<Response<List<UserData>>>()
    fun userDataLive(): LiveData<Response<List<UserData>>> = user

    private val userDB = MutableLiveData<Response<List<UserData>>>()
    fun userDBDataLive(): LiveData<Response<List<UserData>>> = userDB

    val login = MutableLiveData<String>()
    fun loginDataLive(): LiveData<String> = login

    var userList: List<UserData>? = null
    private var itemClicked = false

    private val userDataSourceFactory: UserDataSourceFactory
    private val pageSize = 1
    var userPagedList: LiveData<PagedList<UserData>>

    init {
        val appComponent = GithubProfileApplication.instance.getAppComponent()
        profileRepository = appComponent.profileRepository()
        userDBRepository = appComponent.userDBRepository()

        userDataSourceFactory = UserDataSourceFactory(profileRepository, userDBRepository, compositeDisposable)
        val config = PagedList.Config.Builder().setPageSize(pageSize).setInitialLoadSizeHint(pageSize).setEnablePlaceholders(false).build()
        userPagedList = LivePagedListBuilder(userDataSourceFactory, config).build()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun setImage(data: UserData, itemView: View, binding: ItemUserListBinding) {
        Glide.with(itemView).load(data.avatarUrl).into(binding.avatar)

        /*if ((i + 1).rem(4) == 0 && i > 0 && (i + 1) < data.size) {
            doAsync {
                var bitmap = getBitmapFromURL(data[i].avatarUrl)
                bitmap = invertBitmap(bitmap!!)
                uiThread { Glide.with(itemView).load(bitmap).into(binding.avatar) }
            }.isDone

        } else {
            Glide.with(itemView).load(data[i].avatarUrl).into(binding.avatar)
        }*/
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

    fun itemClickedRes(response: String) {
        fragment.userDetail(response)
        itemClicked = false
    }

    fun recyclerViewVisibility(binding: FragmentUserBinding, state: State, userListAdapter: UserListPagedAdapter) {
        if (listIsEmpty() && state == State.LOADING) {
            loading.value = true
            binding.recyclerView.visibility = View.GONE
            binding.txtError.visibility = View.GONE

        } else if (listIsEmpty() && state == State.ERROR) {
            loading.value = false
            binding.txtError.visibility = View.VISIBLE

        } else {
            loading.value = false
            binding.recyclerView.visibility = View.VISIBLE
            binding.txtError.visibility = View.GONE
        }

        if (!listIsEmpty()) {
            userListAdapter.setState(state ?: State.DONE)
        }
    }

    fun retry() {
        userDataSourceFactory.userDataSourceLiveData.value?.retry()
    }

    fun getState(): LiveData<State> = Transformations.switchMap(
            userDataSourceFactory.userDataSourceLiveData,
            UserDataSource::state
    )

    private fun listIsEmpty(): Boolean {
        return userPagedList.value?.isEmpty() ?: true
    }

    /*fun getUser() {
        loading.value = true

        compositeDisposable.add(
            profileRepository.userList("0")
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
                        user.value = Response.succeed(res)
                    },
                    {
                        userDB()
                    }
                )
        )
    }

    fun userResponse(response: Response<List<UserData>>) {
        try {
            Log.d(LOG_TAG, "DATA STATUS = ${response.status}")

            if (response.status == Status.SUCCEED) {
                if (response.data != null) {
                    userList = response.data
                    userDBRepository.deleteAllAndInsertsList(userList!!)

                    //fragment.setListUser()
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

    private fun userDB() {
        loading.value = true
        compositeDisposable.add(
            userDBRepository.getAllUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { res ->
                        userDB.value = Response.succeed(res)
                    },
                    {
                        userDB.value = when (it) {
                            is NoNetworkException -> {
                                Response.networkLost()
                            }
                            else -> Response.error(it)
                        }
                    }
                )
        )
    }

    fun userDBResponse(response: Response<List<UserData>>) {
        try {
            Log.d(LOG_TAG, "DATA STATUS = ${response.status}")

            if (response.status == Status.SUCCEED) {
                if (!response.data.isNullOrEmpty()) {
                    userList = response.data
                    //fragment.setListUser()

                } else {
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
    }*/
}
