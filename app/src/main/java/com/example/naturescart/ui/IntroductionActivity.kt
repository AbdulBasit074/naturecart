package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.naturescart.R
import com.example.naturescart.adapters.IntroductionViewPagerAdapter
import com.example.naturescart.databinding.ActivityIntroductionBinding
import com.example.naturescart.helper.moveTo

class IntroductionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityIntroductionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_introduction)

        setViewPagerAdapter()
        setListeners()

    }

    private fun setViewPagerAdapter() {

        //Set View pager Adapter for sliding to get updated views
        binding.viewPagerIntroduction.adapter = IntroductionViewPagerAdapter()
        binding.viewPagerIntroduction.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicator.setViewPager2(binding.viewPagerIntroduction)

    }

    private fun setListeners() {

        binding.nextBtn.setOnClickListener {
            moveTo(LanguageSelectionActivity::class.java)
        }

    }

}