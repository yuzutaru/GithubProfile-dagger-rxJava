package com.yuzu.githubprofile.model.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData
import com.yuzu.githubprofile.model.data.ConnectionModel


/**
 * Created by Saurabh Pant medium.com on 24/09/2017
 */

class ConnectionLiveData(context: Context) : LiveData<ConnectionModel?>() {
    private val context: Context
    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(networkReceiver)
    }

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.extras != null) {
                val activeNetwork = intent.extras!![ConnectivityManager.EXTRA_NETWORK_INFO] as NetworkInfo?
                val isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting
                if (isConnected) {
                    when (activeNetwork!!.type) {
                        ConnectivityManager.TYPE_WIFI -> postValue(ConnectionModel(ConnectivityManager.TYPE_WIFI, true))
                        ConnectivityManager.TYPE_MOBILE -> postValue(ConnectionModel(ConnectivityManager.TYPE_MOBILE, true))
                    }
                } else {
                    postValue(ConnectionModel(0, false))
                }
            }
        }
    }

    init {
        this.context = context
    }
}