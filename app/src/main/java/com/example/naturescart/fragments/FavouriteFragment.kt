package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.naturescart.R
import com.example.naturescart.adapters.CategoryViewPagerAdapter
import com.example.naturescart.databinding.FragmentFavouriteBinding
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.google.android.material.tabs.TabLayout

class FavouriteFragment : Fragment(), TabLayout.OnTabSelectedListener,
    ViewPager.OnPageChangeListener {

    private lateinit var binding: FragmentFavouriteBinding
    private var list: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        tabLayoutSetting()
        setListeners()
    }

    private fun setListeners() {
        binding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(activity!!, NotificationActivity::class.java)
        }
        binding.toolBar.profileBtn.setOnClickListener {
            moveFromFragment(activity!!, MenuActivity::class.java)
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
            CategoryViewPagerAdapter(activity!!, childFragmentManager, list.size)


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