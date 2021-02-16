package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityEditProfileBinding
import com.example.naturescart.helper.DialogCustom
import com.example.naturescart.helper.DialogCustomPasswordChange
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService

class EditProfileActivity : AppCompatActivity(), Results {

    private lateinit var binding: ActivityEditProfileBinding
    private var loggedUser: User? = null
    private val updateProfile: Int = 2661

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        loggedUser = NatureDb.newInstance(this).userDao().getLoggedUser()
        binding.user = loggedUser
        setListeners()

    }

    private fun setListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.changePasswordBtn.setOnClickListener {
            DialogCustomPasswordChange(this,loggedUser!!.accessToken) { data -> onPasswordChange(data) }.show()
        }
        binding.saveBtn.setOnClickListener {
            if (isInputOk()) {
                AuthService(updateProfile, this).editProfile(
                    loggedUser!!.accessToken,
                    binding.nameEt.text.toString(),
                    binding.emailEt.text.toString(),
                    binding.phoneNo.text.toString()
                )
            }

        }
    }

    private fun onPasswordChange(data: String) {
        DialogCustom(this, R.drawable.ic_thumb, data).show()
    }

    private fun isInputOk(): Boolean {

        when {
            binding.nameEt.text.isEmpty() -> {
                showToast("Name must required")
                return false
            }
            binding.emailEt.text.isEmpty() -> {
                showToast("Email must required")
                return false
            }
            binding.phoneNo.text.isEmpty() -> {
                showToast("Email must required")
                return false
            }
            else -> return true
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            updateProfile -> {
                DialogCustom(this, R.drawable.ic_thumb, data).show()
                updateProfileLocally()
            }
        }
    }

    private fun updateProfileLocally() {
        loggedUser?.firstName = binding.nameEt.text.toString()
        loggedUser?.email = binding.emailEt.text.toString()
        loggedUser?.phone = binding.phoneNo.text.toString()
        NatureDb.newInstance(this).userDao().logOut()
        NatureDb.newInstance(this).userDao().login(loggedUser!!)
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}