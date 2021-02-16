package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityMenuBinding
import com.example.naturescart.helper.DialogCustom
import com.example.naturescart.helper.moveTo
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson

class MenuActivity : AppCompatActivity(), Results {


    private val registerUserRequest: Int = 122
    private val loginUserRequest: Int = 145
    private lateinit var binding: ActivityMenuBinding
    private var loggedUser: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        loggedUser = NatureDb.newInstance(this).userDao().getLoggedUser()
        setViews()
        setListeners()

    }

    private fun setListeners() {
        binding.signInBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        binding.signInBottomSheet.registerNewBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
                BottomSheetBehavior.STATE_COLLAPSED

            BottomSheetBehavior.from(binding.registerBottomSheet.parent).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.registerBottomSheet.loginBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
                BottomSheetBehavior.STATE_EXPANDED

            BottomSheetBehavior.from(binding.registerBottomSheet.parent).state =
                BottomSheetBehavior.STATE_COLLAPSED

        }

        binding.profileBtn.setOnClickListener {
            moveTo(UserDetailActivity::class.java)
        }
        binding.registerBottomSheet.registerBtn.setOnClickListener {

            if (isRegisterInputOk()) {
                AuthService(registerUserRequest, this).userRegister(
                    binding.registerBottomSheet.fullNameEt.text.toString(),
                    binding.registerBottomSheet.emailEtRegister.text.toString(),
                    binding.registerBottomSheet.passwordEtRegister.text.toString(),
                    getString(R.string.country_phone_code) + binding.registerBottomSheet.phoneNo.text.toString()
                )
            }
        }
        binding.signInBottomSheet.loginBtn.setOnClickListener {

            if (isLoginInputOk()) {

                AuthService(
                    loginUserRequest,
                    this
                ).userLogin(
                    binding.signInBottomSheet.emailEt.text.toString(),
                    binding.signInBottomSheet.passwordEt.text.toString()
                )


            }

        }
    }

    private fun isRegisterInputOk(): Boolean {
        when {
            binding.registerBottomSheet.fullNameEt.text.isEmpty() -> {
                showToast("Full name must required")
                return false
            }

            binding.registerBottomSheet.emailEtRegister.text.isEmpty() -> {
                showToast("Email name must required")
                return false
            }
            binding.registerBottomSheet.passwordEtRegister.text.isEmpty() -> {
                showToast("Password must required")
                return false
            }

            binding.registerBottomSheet.phoneNo.text.isEmpty() -> {
                showToast("Phone must required")
                return false
            }
            else -> return true
        }

    }

    private fun isLoginInputOk(): Boolean {

        return when {
            binding.signInBottomSheet.emailEt.text.isEmpty() -> {
                showToast("Email must required")
                false
            }
            binding.signInBottomSheet.passwordEt.text.isEmpty() -> {
                showToast("Email must required")
                false
            }
            else -> true

        }


    }

    private fun setViews() {
        BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
            BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            registerUserRequest -> {
                DialogCustom(this, R.drawable.ic_thumb, "Account Register").show()
            }
            loginUserRequest -> {
                loggedUser = Gson().fromJson(data, User::class.java)
                NatureDb.newInstance(this).userDao().logOut()
                NatureDb.newInstance(this).userDao().login(loggedUser!!)
                onBackPressed()
            }
        }

    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)

    }
}