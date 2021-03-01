package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityLanguageSelectionBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.moveTo

class LanguageSelectionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLanguageSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_selection)
        setListeners()


    }

    private fun setListeners() {
        binding.englishBtn.setOnClickListener { checkStatus() }
        binding.arabicBtn.setOnClickListener { checkStatus() }
    }

    fun checkStatus() {

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.onBoardingShow, true)
        )
            moveTo(IntroductionActivity::class.java)
        else
            moveTo(HomeActivity::class.java)
    }
}