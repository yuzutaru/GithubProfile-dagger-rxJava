package com.yuzu.githubprofile.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.yuzu.githubprofile.GithubProfileApplication
import com.yuzu.githubprofile.databinding.FragmentUserBinding
import com.yuzu.githubprofile.databinding.ItemUserListBinding
import com.yuzu.githubprofile.model.data.ConnectionLiveData
import com.yuzu.githubprofile.model.data.ConnectionModel
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.State
import com.yuzu.githubprofile.model.network.datasource.UserDBDataSourceFactory
import com.yuzu.githubprofile.model.network.datasource.UserDataSource
import com.yuzu.githubprofile.model.network.datasource.UserDataSourceFactory
import com.yuzu.githubprofile.model.network.repository.NotesDBRepository
import com.yuzu.githubprofile.model.network.repository.ProfileRepository
import com.yuzu.githubprofile.model.network.repository.UserDBRepository
import com.yuzu.githubprofile.view.adapter.UserListPagedAdapter
import com.yuzu.githubprofile.view.fragment.UserFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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
    private val notesDBRepository: NotesDBRepository
    private val userDataSourceFactory: UserDataSourceFactory
    private var userDBDataSourceFactory: UserDBDataSourceFactory? = null

    val login = MutableLiveData<String>()
    fun loginDataLive(): LiveData<String> = login

    var userPagedList: LiveData<PagedList<UserData>>
    var search = MutableLiveData<String>()

    lateinit var connectionLiveData: ConnectionLiveData

    private var itemClicked = false
    private val pageSize = 1

    init {
        val appComponent = GithubProfileApplication.instance.getAppComponent()
        profileRepository = appComponent.profileRepository()
        userDBRepository = appComponent.userDBRepository()
        notesDBRepository = appComponent.notesDBRepository()

        userDataSourceFactory = UserDataSourceFactory(profileRepository, userDBRepository, compositeDisposable)
        val config = PagedList.Config.Builder().setPageSize(pageSize).setInitialLoadSizeHint(pageSize).setEnablePlaceholders(false).build()
        //userPagedList = LivePagedListBuilder(userDataSourceFactory, config).build()

        userPagedList = Transformations.switchMap(search) { input ->
            return@switchMap if (input == null || input.equals("") || input.equals("%%")) {
                //check if the current value is empty load all data else search
                LivePagedListBuilder(userDataSourceFactory, config).build()
            } else {
                userDBDataSourceFactory = UserDBDataSourceFactory(userDBRepository, compositeDisposable, input)
                LivePagedListBuilder(userDBDataSourceFactory!!, config).build()
            }
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun setImage(i: Int, data: UserData, binding: ItemUserListBinding) {

        if ((i + 1).rem(4) == 0 && i > 0) {
            doAsync {
                var bitmap = getBitmapFromURL(data.avatarUrl)
                bitmap = invertBitmap(bitmap!!)
                Log.e(LOG_TAG, "BITMAP INVERTED = $i")
                uiThread { Glide.with(fragment.requireActivity()).load(bitmap).into(binding.avatar) }
            }.isDone

        } else {
            Glide.with(fragment.requireActivity()).load(data.avatarUrl).diskCacheStrategy(DiskCacheStrategy.DATA).into(binding.avatar)
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
        /**
         * Created by Joel Sjögren Stackoverflow on 07/08/2012
         */

        val RGB_MASK = 0x00FFFFFF

        // Create mutable Bitmap to invert, argument true makes it mutable
        val inversion: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = inversion.width
        val height = inversion.height
        val pixels = width * height

        // Get original pixels
        val pixel = IntArray(pixels)
        inversion.getPixels(pixel, 0, width, 0, 0, width, height)

        // Modify pixels
        for (i in 0 until pixels) pixel[i] = pixel[i] xor RGB_MASK
        inversion.setPixels(pixel, 0, width, 0, 0, width, height)

        // Return inverted Bitmap
        return inversion
    }

    fun setNote(binding: ItemUserListBinding, data: UserData) {
        if (data.login != null) {
            compositeDisposable.add(
                    notesDBRepository.get(data.login!!)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { response ->
                                        if (response.notes != null) {
                                            userDBRepository.updateNotes(data.id, response.notes!!)
                                            binding.notes.visibility = View.VISIBLE
                                        }

                                    }, {
                                binding.notes.visibility = View.GONE
                            }
                            )
            )
        }
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
            userListAdapter.setState(state)
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

    fun connection(connection: ConnectionModel?) {
        if (connection != null) {
            if (connection.isConnected) {
                when (connection.type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        Toast.makeText(fragment.context, String.format("Wifi turned ON"), Toast.LENGTH_SHORT).show()
                        retry()
                    }
                    ConnectivityManager.TYPE_MOBILE -> {
                        Toast.makeText(fragment.context, String.format("Mobile data turned ON"), Toast.LENGTH_SHORT).show()
                        retry()
                    }
                }
            } else {
                Toast.makeText(fragment.context, String.format("Connection turned OFF"), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
