package com.example.naturescart.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.DialogViewBinding

class DialogCustom(context: Context, private val iconId: Int, private val title: String) :
    Dialog(context) {


    private lateinit var binding: DialogViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_view, null, false)
        this.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        Glide.with(context).load(iconId).into(binding.iconDialog)
        binding.title.text = title
    }

}