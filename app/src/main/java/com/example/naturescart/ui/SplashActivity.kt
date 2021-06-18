package com.example.naturescart.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.BuildConfig
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivitySplashBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.OnBoarding
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SplashActivity : AppCompatActivity(), Results {

    private lateinit var binding: ActivitySplashBinding
    private val onBoardRequest = 1293
    private val translationRequest = 1221
    private var counter = 0
    private lateinit var onBoardingList: ArrayList<OnBoarding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        checkLatestVersion()
    }

    private fun moveForward() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected = connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
        if (!isConnected) {
            startActivity(NoInternetActivity.newInstance(this, true))
        } else {
            DataService(translationRequest, this).getTranslation()
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            translationRequest -> {
                TranslationsHelper.getInstance(this).storeTranslations(data)
                onBoardData()
            }
            onBoardRequest -> {
                Persister.with(this).persist(Constants.onBoardingPersistenceKey, data)
                onBoardingList = Gson().fromJson(data, object : TypeToken<ArrayList<OnBoarding>>() {}.type)
                onBoardingList.forEach {
                    ImagesCache().loadImage(this, it.image) { onLoadCompleted() }
                }
            }
        }
    }

    private fun onBoardData() {
        val onBoardingData = Persister.with(this).getPersisted(Constants.onBoardingPersistenceKey)
        if (onBoardingData == null)
            DataService(onBoardRequest, this).onBoarding()
        else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.languageSelected, false))
                    moveToWithoutHistory(HomeActivity::class.java)
                else
                    moveToWithoutHistory(LanguageSelectionActivity::class.java)

            }, Constants.splashTime)
        }
    }

    override fun onFailure(requestCode: Int, data: String) {

    }

    private fun onLoadCompleted() {
        if (++counter == onBoardingList.size) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.languageSelected, false))
                moveToWithoutHistory(HomeActivity::class.java)
            else
                moveToWithoutHistory(LanguageSelectionActivity::class.java)
        }
    }

    private fun checkLatestVersion() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val defaultMap: HashMap<String, Any> = HashMap()
        val versionCodeKey = "latest_app_version"
        defaultMap[versionCodeKey] = BuildConfig.VERSION_CODE
        remoteConfig.setDefaultsAsync(defaultMap as Map<String, Any>)
        remoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build())
        remoteConfig.fetch().addOnCompleteListener {
            if (it.isSuccessful) {
                remoteConfig.activate()
                remoteConfig.getDouble(versionCodeKey).toInt()
                moveForward()
            } else {
                moveForward()
            }
        }
    }

}