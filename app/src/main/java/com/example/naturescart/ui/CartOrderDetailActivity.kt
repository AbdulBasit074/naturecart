package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityCartOrderDetailBinding
import com.example.naturescart.helper.moveTo
import com.example.naturescart.helper.moveToIntent

class CartOrderDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityCartOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_order_detail)
        setListener()
    }

    private fun setListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.selectedAddressContainer.setOnClickListener {
            moveToIntent(AddressActivity.newInstance(this,true))
        }
        binding.bottomSheetCO.Btn.setOnClickListener {
            moveTo(OrderPlacedActivity::class.java)
        }
    }
}