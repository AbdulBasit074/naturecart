package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityEditProfileBinding
import com.example.naturescart.helper.*
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
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        binding.user = loggedUser
        setListeners()

    }

    private fun setListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.changePasswordBtn.setOnClickListener {
            DialogCustomPasswordChange(this, loggedUser!!.accessToken) { data ->
                onPasswordChange(
                    data
                )
            }.show()

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

        return when {
            binding.nameEt.text.isEmpty() -> {

                showToast(Constants.getTranslate(this, "name_req"))
                false
            }
            binding.emailEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "email_required"))
                false
            }
            binding.phoneNo.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "phone_req"))
                false
            }
            else -> true
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
        NatureDb.getInstance(this).userDao().logOut()
        NatureDb.getInstance(this).userDao().login(loggedUser!!)
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}