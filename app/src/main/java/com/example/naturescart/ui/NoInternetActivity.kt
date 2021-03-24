package com.example.naturescart.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityNoInternetBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.moveToWithoutHistory

class NoInternetActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityNoInternetBinding
    private var isConnected = true
    private var monitoringConnectivity = false
    private var shouldMoveToHome = false

    companion object {
        fun newInstance(context: Context, shouldMoveToHome: Boolean = false): Intent {
            val intent = Intent(context, NoInternetActivity::class.java)
            intent.putExtra(Constants.dataPassKey, shouldMoveToHome)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_no_internet)
        shouldMoveToHome = intent.getBooleanExtra(Constants.dataPassKey, false)
        initView()
    }

    fun initView() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        isConnected =
            connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
        mBinding.retryBtn.setOnClickListener {
            if (isConnected) {
                if (shouldMoveToHome) {
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.languageSelected, false))
                        moveToWithoutHistory(HomeActivity::class.java)
                    else
                        moveToWithoutHistory(LanguageSelectionActivity::class.java)
                } else {
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerInternetCallback()
    }

    override fun onPause() {
        super.onPause()
        unregisterInternetCallback()
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

    private fun unregisterInternetCallback() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(connectivityCallback)
        monitoringConnectivity = false
    }

    @SuppressLint("NewApi")
    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isConnected = true
        }

        override fun onLost(network: Network) {
            isConnected = false
        }
    }

    override fun onBackPressed() {

    }
}