package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityUserDetailBinding
import com.example.naturescart.helper.moveTo
import com.example.naturescart.helper.moveToIntent
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService

class UserDetailActivity : AppCompatActivity(), Results {

    private lateinit var binding: ActivityUserDetailBinding
    private var loggedUser: User? = null
    private val logoutRequest: Int = 222

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)
        loggedUser = NatureDb.newInstance(this).userDao().getLoggedUser()
        binding.user = loggedUser
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
        binding.logout.setOnClickListener {
            AuthService(logoutRequest, this).userLogout(loggedUser!!.accessToken)
        }

    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            logoutRequest -> {
                NatureDb.newInstance(this).userDao().logOut()
                onBackPressed()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}