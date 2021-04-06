package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityOrderPlacedBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.moveTo
import com.example.naturescart.helper.setLanguage

class OrderPlacedActivity : AppCompatActivity() {


    private lateinit var binding: ActivityOrderPlacedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_placed)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(Constants.cartID, 0).apply()

        binding.Btn.setOnClickListener {
            moveTo(HomeActivity::class.java)
            finishAffinity()
        }


    }
}