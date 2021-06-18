package com.example.naturescart.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityOrderPlacedBinding
import com.example.naturescart.helper.*
import org.greenrobot.eventbus.EventBus

class OrderPlacedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderPlacedBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_placed)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(Constants.cartID, 0).apply()
        EventBus.getDefault().postSticky(OrderPlaceEvent())
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            moveTo(HomeActivity::class.java)
            Constants.orderPlace = true
            finishAffinity()
        }
        handler.postDelayed(runnable, 2000)
        binding.Btn.setOnClickListener {
            moveTo(HomeActivity::class.java)
            Constants.orderPlace = true
            finishAffinity()
        }
    }
    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }
}