package com.example.naturescart.helper

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import org.greenrobot.eventbus.EventBus

class NatureApp : Application() {

    private var isConnected = true
    private var monitoringConnectivity = false

    override fun onCreate() {
        super.onCreate()
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        isConnected =
            connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
        if (!isConnected) {
            EventBus.getDefault().postSticky(ConnectivityEvent(false))
        }
        registerInternetCallback()
    }

    private fun registerInternetCallback() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
            connectivityCallback
        )
        monitoringConnectivity = true
    }

    @SuppressLint("NewApi")
    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isConnected = true
            EventBus.getDefault().postSticky(ConnectivityEvent(true))
        }

        override fun onLost(network: Network) {
            isConnected = false
            EventBus.getDefault().postSticky(ConnectivityEvent(false))
        }
    }
}