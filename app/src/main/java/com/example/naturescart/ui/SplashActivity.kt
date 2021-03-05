package com.example.naturescart.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivitySplashBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.Persister
import com.example.naturescart.helper.moveToWithoutHistory
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import org.greenrobot.eventbus.EventBus

class SplashActivity : AppCompatActivity(),Results {

    private lateinit var binding: ActivitySplashBinding
    private val deviceAddRequest: Int = 1293

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)


        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected =
            connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
        if (!isConnected) {
            startActivity(NoInternetActivity.newInstance(this, true))
        } else {


            /**Splash call for 3sec and open home screen*/
            Handler(Looper.getMainLooper()).postDelayed({
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.languageSelected, false))
                    moveToWithoutHistory(HomeActivity::class.java)
                else
                    moveToWithoutHistory(LanguageSelectionActivity::class.java)

            }, Constants.splashTime)

        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
    }

    override fun onFailure(requestCode: Int, data: String) {
    }
}