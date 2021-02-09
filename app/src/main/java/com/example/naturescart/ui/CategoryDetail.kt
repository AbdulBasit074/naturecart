package com.example.naturescart.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.example.naturescart.R
import com.example.naturescart.adapters.CategoryViewPagerAdapter
import com.example.naturescart.databinding.ActivityCategoryDetailBinding
import com.google.android.material.tabs.TabLayout

class CategoryDetail : AppCompatActivity(), TabLayout.OnTabSelectedListener,
    ViewPager.OnPageChangeListener {


    private lateinit var binding: ActivityCategoryDetailBinding
    private var list: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_detail)
        setData()
        tabLayoutSetting()
        setListeners()
    }

    private fun setListeners() {
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setData() {
        list.add("All")
        list.add("Fruit")
        list.add("Vegetable")
        list.add("Fresh Herbs")
    }

    private fun tabLayoutSetting() {

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        list.forEach {

            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it).setTag(it))

        }


        binding.tabLayout.addOnTabSelectedListener(this)

        binding.viewPager.adapter =
            CategoryViewPagerAdapter(this, supportFragmentManager, list.size)


        binding.viewPager.addOnPageChangeListener(this)
        onPageSelected(0)






        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }
        })


    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        var selectedIndex = 0
        for (i in 0 until list.size) {
            if (list[i] == tab!!.tag)
                selectedIndex = i
        }
        binding.viewPager.currentItem = selectedIndex
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        binding.tabLayout.getTabAt(position)!!.select()

    }
}