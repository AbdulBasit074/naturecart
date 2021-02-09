package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityMenuBinding
import com.example.naturescart.helper.moveTo
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MenuActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
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

        binding.profileBtn.setOnClickListener {
            moveTo(UserDetailActivity::class.java)
        }


    }

    private fun setViews() {
        BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
            BottomSheetBehavior.STATE_COLLAPSED
    }
}