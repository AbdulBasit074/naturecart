package com.example.naturescart.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivitySplashBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.moveToWithoutHistory

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        /**Splash call for 3sec and open home screen*/
        Handler(Looper.getMainLooper()).postDelayed({
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.languageSelected, false))
                moveToWithoutHistory(HomeActivity::class.java)
            else
                moveToWithoutHistory(LanguageSelectionActivity::class.java)
        }, Constants.splashTime)
    }
}