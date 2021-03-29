package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityLanguageSelectionBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.TranslationsHelper
import com.example.naturescart.helper.moveToWithoutHistory
import com.example.naturescart.helper.setLanguage

class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_selection)
        setListeners()
    }

    private fun setListeners() {
        binding.englishBtn.setOnClickListener {
            TranslationsHelper.getInstance(this).setLanguage("en")
            setLanguage()
            checkStatus()
        }
        binding.arabicBtn.setOnClickListener {
            TranslationsHelper.getInstance(this).setLanguage("ar")
            checkStatus()
        }
    }

    private fun checkStatus() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.languageSelected, true).apply()
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.onBoardingShow, true))
            moveToWithoutHistory(IntroductionActivity::class.java)
        else
            moveToWithoutHistory(HomeActivity::class.java)
    }
}