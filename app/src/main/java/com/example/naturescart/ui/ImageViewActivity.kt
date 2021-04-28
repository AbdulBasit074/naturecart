package com.example.naturescart.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.adapters.setImage
import com.example.naturescart.databinding.ActivityImageViewBinding
import com.example.naturescart.helper.Constants

class ImageViewActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityImageViewBinding

    companion object {
        fun newInstance(context: Context, iconUrl: String): Intent {
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra(Constants.dataPassKey, iconUrl)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_view)
        val iconUrl = intent.getStringExtra(Constants.dataPassKey)!!
        setImage(mBinding.iconIv, iconUrl)
        mBinding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }
}