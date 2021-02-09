package com.example.naturescart.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityOrderPlacedBinding
import com.example.naturescart.helper.DialogCustom

class OrderPlacedActivity : AppCompatActivity() {


    private lateinit var binding: ActivityOrderPlacedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_placed)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            val dialog = DialogCustom(
                binding.root.context,
                R.drawable.ic_thumb,
                "Order Placed"
            )
            dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
            dialog.show()


        }, 2000)
    }
}