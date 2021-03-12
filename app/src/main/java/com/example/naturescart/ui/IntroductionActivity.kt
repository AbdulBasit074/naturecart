package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.example.naturescart.R
import com.example.naturescart.adapters.IntroductionViewPagerAdapter
import com.example.naturescart.databinding.ActivityIntroductionBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.Persister
import com.example.naturescart.helper.moveToWithoutHistory
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.OnBoarding
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import com.example.naturescart.services.data.DataService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IntroductionActivity : AppCompatActivity(), Results {

    private lateinit var binding: ActivityIntroductionBinding
    private var boardList: ArrayList<OnBoarding> = ArrayList()
    private val onBoardRequest: Int = 1293

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_introduction)
        val data = Persister.with(this).getPersisted(Constants.onBoardingPersistenceKey)
        if (data == null)
            DataService(onBoardRequest, this).onBoarding()
        else
            onSuccess(onBoardRequest, data)
        setViewPagerAdapter()
        setListeners()

    }

    private fun setViewPagerAdapter() {
        //Set View pager Adapter for sliding to get updated views
        binding.viewPagerIntroduction.adapter = IntroductionViewPagerAdapter(boardList)
        binding.viewPagerIntroduction.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicator.setViewPager2(binding.viewPagerIntroduction)
    }

    private fun setListeners() {
        binding.nextBtn.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.onBoardingShow, false).apply()
            moveToWithoutHistory(HomeActivity::class.java)
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            onBoardRequest -> {
                boardList.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<OnBoarding>>() {}.type))
                binding.viewPagerIntroduction.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}