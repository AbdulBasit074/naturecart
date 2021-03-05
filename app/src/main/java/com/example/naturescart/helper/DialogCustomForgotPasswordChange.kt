package com.example.naturescart.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ForgotPasswordChangeBinding
import com.example.naturescart.databinding.PasswordChangeBinding
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService

class DialogCustomForgotPasswordChange(context: Context, private val email: String, private val onSuccess: (String) -> Unit) :
    Dialog(context), Results {


    private lateinit var binding: ForgotPasswordChangeBinding
    private val onChangePassword: Int = 1331
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.forgot_password_change,
                null,
                false
            )
        this.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.changeBtn.setOnClickListener {
            if (isInputOk()) {
                AuthService(onChangePassword, this).resetPassword(
                    email,
                    binding.newEt.text.toString(),
                    binding.confirmEt.text.toString()
                )
            }
        }
    }

    private fun isInputOk(): Boolean {
        when {
            binding.newEt.text.isEmpty() -> {
                binding.newEt.context.showToast("New password must Required")
                return false
            }
            binding.confirmEt.text.isEmpty() -> {
                binding.confirmEt.context.showToast("Confirm password must Required")
                return false
            }
            else -> return true
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        onSuccess(data)
        this.dismiss()
    }

    override fun onFailure(requestCode: Int, data: String) {
        binding.newEt.context.showToast(data)
    }
}