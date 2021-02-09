package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityUserDetailBinding
import com.example.naturescart.helper.moveTo
import com.example.naturescart.helper.moveToIntent

class UserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)
        setListeners()


    }

    private fun setListeners() {
        binding.editProfileBtn.setOnClickListener {
            moveTo(EditProfileActivity::class.java)
        }
        binding.logout.setOnClickListener {

        }
        binding.manageAddressBtn.setOnClickListener {
            moveToIntent(AddressActivity.newInstance(this, false))
        }
        binding.backBtn.setOnClickListener { onBackPressed() }


    }
}