package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityLanguageSelectionBinding
import com.example.naturescart.helper.moveTo

class LanguageSelectionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLanguageSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_selection)
        setListeners()


    }

    private fun setListeners() {
        binding.englishBtn.setOnClickListener { moveTo(HomeActivity::class.java) }
        binding.arabicBtn.setOnClickListener { moveTo(HomeActivity::class.java) }
    }
}