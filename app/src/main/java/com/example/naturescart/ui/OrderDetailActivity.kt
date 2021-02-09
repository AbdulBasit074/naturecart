package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.adapters.OrderItemRvAdapter
import com.example.naturescart.databinding.ActivityOrderDetailBinding
import com.example.naturescart.helper.HorizantalMidDivider

class OrderDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityOrderDetailBinding
    private var list: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        setData()
        setAdapter()
        setListeners()

    }

    private fun setListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setData() {
        list.add("Dummy Data")
        list.add("Dummy Data")
        list.add("Dummy Data")
        list.add("Dummy Data")
        list.add("Dummy Data")
    }

    private fun setAdapter() {
        binding.itemsRv.adapter = OrderItemRvAdapter(list)
        binding.itemsRv.addItemDecoration(
            HorizantalMidDivider(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                )
            )
        )
    }
}