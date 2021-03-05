package com.example.naturescart.ui

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityMenuBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import com.example.naturescart.services.product.ProductService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class MenuActivity : AppCompatActivity(), Results {

    private val registerUserRequest: Int = 122
    private val loginUserRequest: Int = 145
    private val productFavouriteRc: Int = 2389
    private val forgotPasswordRq: Int = 2459
    private val deviceAddRequest: Int = 1293
    private val otpPasswordRq: Int = 1159
    private var loadingView: LoadingDialog? = null
    private val otpArray = arrayOfNulls<String>(4)
    private lateinit var binding: ActivityMenuBinding
    private var loggedUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)

        loadingView = LoadingDialog(this)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        setViews()
        setListeners()
        if (loggedUser == null)
            binding.profileBtn.visibility = View.GONE
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
        binding.signInBottomSheet.forgotBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.forgotPasswordEmailBS.parent).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        binding.forgotPasswordEmailBS.submitBtn.setOnClickListener {
            if (binding.forgotPasswordEmailBS.emailInput.text.isNotEmpty()) {
                loadingView?.show()
                AuthService(forgotPasswordRq, this).forgotPassword(binding.forgotPasswordEmailBS.emailInput.text.toString())
            } else
                showToast("Email is required")
        }
        binding.forgotPasswordEmailBS.loginBtn.setOnClickListener {

            BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
                BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(binding.registerBottomSheet.parent).state =
                BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.from(binding.forgotPasswordEmailBS.parent).state =
                BottomSheetBehavior.STATE_COLLAPSED
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
                loadingView?.show()
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
                loadingView?.show()
                AuthService(loginUserRequest, this).userLogin(
                    binding.signInBottomSheet.emailEt.text.toString(),
                    binding.signInBottomSheet.passwordEt.text.toString(),
                    Persister.with(this).getPersisted(Constants.fcmTokenPersistenceKey, "").toString()
                )
            }
        }
        binding.otpVerifyBS.verifyBtn.setOnClickListener {
            loadingView?.show()
            AuthService(otpPasswordRq, this).verifyOtp(binding.forgotPasswordEmailBS.emailInput.text.toString(), otpArray.convertToString().toInt())
        }
        binding.otpVerifyBS.digit1Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.otpVerifyBS.digit2Et.requestFocus()
                    otpArray[0] = binding.otpVerifyBS.digit1Et.text.toString()
                }
            }
        })
        binding.otpVerifyBS.digit2Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.otpVerifyBS.digit3Et.requestFocus()
                    otpArray[1] = binding.otpVerifyBS.digit2Et.text.toString()
                } else {
                    binding.otpVerifyBS.digit1Et.requestFocus()
                }
            }
        })
        binding.otpVerifyBS.digit3Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.otpVerifyBS.digit4Et.requestFocus()
                    otpArray[2] = binding.otpVerifyBS.digit3Et.text.toString()
                } else {
                    binding.otpVerifyBS.digit2Et.requestFocus()
                }
            }
        })
        binding.otpVerifyBS.digit4Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    otpArray[3] = binding.otpVerifyBS.digit4Et.text.toString()
                    loadingView?.show()
                    AuthService(otpPasswordRq, this@MenuActivity).verifyOtp(binding.forgotPasswordEmailBS.emailInput.text.toString(), otpArray.convertToString().toInt())
                    hideKeyboard()
                } else {
                    binding.otpVerifyBS.digit3Et.requestFocus()
                }
            }
        })


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
        loadingView?.dismiss()

        when (requestCode) {
            registerUserRequest -> {
                AuthService(loginUserRequest, this).userLogin(
                    binding.registerBottomSheet.emailEtRegister.text.toString(),
                    binding.registerBottomSheet.passwordEtRegister.text.toString(),
                    Persister.with(this).getPersisted(Constants.fcmTokenPersistenceKey, "").toString()
                )
            }
            loginUserRequest -> {
                saveUserDetail(data)
            }
            forgotPasswordRq -> {
                showToast(data)
                BottomSheetBehavior.from(binding.otpVerifyBS.parent).state = BottomSheetBehavior.STATE_EXPANDED
                binding.otpVerifyBS.digit1Et.requestFocus()
                showKeyboard()

            }
            otpPasswordRq -> {
                BottomSheetBehavior.from(binding.otpVerifyBS.parent).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                BottomSheetBehavior.from(binding.forgotPasswordEmailBS.parent).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                binding.otpVerifyBS.digit1Et.text.clear()
                binding.otpVerifyBS.digit2Et.text.clear()
                binding.otpVerifyBS.digit3Et.text.clear()
                binding.otpVerifyBS.digit4Et.text.clear()
                hideKeyboard()
                DialogCustomForgotPasswordChange(this, binding.forgotPasswordEmailBS.emailInput.text.toString()) { data ->
                    onPasswordChange(
                        data
                    )
                }.show()
            }
        }
    }

    private fun onPasswordChange(data: String) {
        BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
            BottomSheetBehavior.STATE_EXPANDED
        DialogCustom(this, R.drawable.ic_thumb, data).show()
    }

    private fun saveUserDetail(data: String) {
        loggedUser = Gson().fromJson(data, User::class.java)
        NatureDb.getInstance(this).userDao().logOut()
        NatureDb.getInstance(this).userDao().login(loggedUser!!)
        val favorites = NatureDb.getInstance(this).favouriteDao().getAllProduct()
        favorites.forEach {
            ProductService(productFavouriteRc, this).addToFavourite(loggedUser?.accessToken ?: "", it.id ?: 0)
        }
        EventBus.getDefault().postSticky(LogInEvent())
        if (callingActivity == null) {
            moveTo(UserDetailActivity::class.java)
            finish()
        } else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        loadingView?.dismiss()
        showToast(data)
    }
}
