package com.example.naturescart.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.PasswordChangeBinding

class DialogCustomPasswordChange(
    context: Context
) :
    Dialog(context) {


    private lateinit var binding: PasswordChangeBinding
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
        binding.changeBtn.setOnClickListener { this.dismiss() }
        binding.closeBtn.setOnClickListener { this.dismiss() }

    }

}