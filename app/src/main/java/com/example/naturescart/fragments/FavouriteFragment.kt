package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.naturescart.R
import com.example.naturescart.adapters.FavouriteViewPagerAdapter
import com.example.naturescart.databinding.FragmentFavouriteBinding
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.model.Category
import com.example.naturescart.model.Product
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.example.naturescart.ui.UserDetailActivity
import com.google.android.material.tabs.TabLayout

class FavouriteFragment : Fragment(), TabLayout.OnTabSelectedListener,
    ViewPager.OnPageChangeListener {

    private lateinit var binding: FragmentFavouriteBinding
    private var list: ArrayList<String> = ArrayList()
    private var loggedUser: User? = null
    private  var allCategoryList: ArrayList<Category> = ArrayList()
    private lateinit var allProductList: ArrayList<Product>
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
        loggedUser = NatureDb.newInstance(requireActivity()).userDao().getLoggedUser()
        if (loggedUser == null)
            getVisitorFavourite()

        setListeners()
    }

    private fun getVisitorFavourite() {
        allCategoryList.add(Category())
        allCategoryList.addAll(
            NatureDb.newInstance(requireActivity()).favouriteDao()
                .getAllCategory() as ArrayList<Category>
        )
        allProductList = NatureDb.newInstance(requireActivity()).favouriteDao()
            .getAllProduct() as ArrayList<Product>
        tabLayoutSetting()
    }


    private fun setListeners() {
        binding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(requireActivity(), NotificationActivity::class.java)
        }
        binding.toolBar.profileBtn.setOnClickListener {
            if (loggedUser == null)
                moveFromFragment(requireActivity(), MenuActivity::class.java)
            else
                moveFromFragment(requireActivity(), UserDetailActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        loggedUser = NatureDb.newInstance(requireActivity()).userDao().getLoggedUser()
    }


    private fun tabLayoutSetting() {

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        allCategoryList.forEach {

            if (it.name == "All")
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All").setTag(it.id))
            else
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.name).setTag(it.id))
        }

        binding.tabLayout.addOnTabSelectedListener(this)
        binding.viewPager.adapter = FavouriteViewPagerAdapter(
            requireActivity(),
            childFragmentManager,
            allCategoryList.size,
            allCategoryList
        )

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
            if (allCategoryList[i] == tab!!.tag)
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