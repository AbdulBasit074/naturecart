package com.example.naturescart.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.PasswordChangeBinding
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService

class DialogCustomPasswordChange(
    context: Context, private val userToken: String, private val onSuccess: (String) -> Unit
) :
    Dialog(context), Results {


    private lateinit var binding: PasswordChangeBinding
    private val onChangePassword: Int = 331
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.password_change,
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
                AuthService(onChangePassword, this).changePassword(
                    userToken,
                    binding.currentEt.text.toString(),
                    binding.newEt.text.toString(),
                    binding.confirmEt.text.toString()
                )
            }

        }
        binding.closeBtn.setOnClickListener { this.dismiss() }

    }

    private fun isInputOk(): Boolean {
        when {
            binding.currentEt.text.isEmpty() -> {
                binding.currentEt.context.showToast("Current password must Required")
                return false
            }
            binding.newEt.text.isEmpty() -> {
                binding.currentEt.context.showToast("New password must Required")
                return false
            }
            binding.confirmEt.text.isEmpty() -> {
                binding.currentEt.context.showToast("Confirm password must Required")
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
        binding.currentEt.context.showToast(data)
    }

}