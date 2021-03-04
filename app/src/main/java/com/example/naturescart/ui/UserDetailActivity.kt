package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityUserDetailBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService

class UserDetailActivity : AppCompatActivity(), Results {

    private lateinit var binding: ActivityUserDetailBinding
    private var loggedUser: User? = null
    private val logoutRequest: Int = 222
    private val pickImageRequest: Int = 3331
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        binding.user = loggedUser
        setListeners()
    }

    private fun setListeners() {
        binding.editProfileBtn.setOnClickListener {
            moveTo(EditProfileActivity::class.java)
        }

        binding.manageAddressBtn.setOnClickListener {
            moveToIntent(AddressActivity.newInstance(this, false))
        }
        binding.backBtn.setOnClickListener { onBackPressed() }
        binding.logout.setOnClickListener {
            AuthService(logoutRequest, this).userLogout(loggedUser!!.accessToken)
        }
        binding.profileBtn.setOnClickListener {
            if (hasStoragePermission())

            else
                requestedStoragePermission(pickImageRequest)


        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            logoutRequest -> {
                NatureDb.getInstance(this).userDao().logOut()
                moveTo(HomeActivity::class.java)
                finishAffinity()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}