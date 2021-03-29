package com.example.naturescart.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityOrderPlacedBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.DialogCustom
import com.example.naturescart.helper.moveTo
import com.example.naturescart.helper.setLanguage

class OrderPlacedActivity : AppCompatActivity() {


    private lateinit var binding: ActivityOrderPlacedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_placed)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(Constants.cartID, 0).apply()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val dialog = DialogCustom(
                binding.root.context,
                R.drawable.ic_thumb,
                Constants.getTranslate(this, "order_placed")
            )
            dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
            dialog.show()
        }, 2000)
        binding.Btn.setOnClickListener {
            moveTo(HomeActivity::class.java)
            finishAffinity()
        }


    }
}