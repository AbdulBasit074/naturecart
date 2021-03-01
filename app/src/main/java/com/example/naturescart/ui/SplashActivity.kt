package com.example.naturescart.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivitySplashBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.moveTo

class SplashActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        /**Splash call for 3sec and open home screen*/
        Handler(Looper.getMainLooper()).postDelayed({
            moveTo(LanguageSelectionActivity::class.java)
        }, Constants.splashTime)


    }
}