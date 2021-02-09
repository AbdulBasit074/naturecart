package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.naturescart.R
import com.example.naturescart.adapters.BatchAdapterRv
import com.example.naturescart.adapters.CategoryAdapterRv
import com.example.naturescart.adapters.HomeSliderViewPagerAdapter
import com.example.naturescart.adapters.ItemAdapterRv
import com.example.naturescart.databinding.FragmentHomeBinding
import com.example.naturescart.helper.HorizantalDivider
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.ui.CategoryDetail
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity


class HomeFragment : Fragment() {

    companion object {
        var dummyList: ArrayList<String> = ArrayList()
    }


    private lateinit var homeBinding: FragmentHomeBinding
    private var sliderList: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setDummyData()
        setListeners()
    }

    private fun setListeners() {

        homeBinding.seeAllFruits.setOnClickListener {
            moveFromFragment(activity!!, CategoryDetail::class.java)
        }
        homeBinding.seeAllVegetable.setOnClickListener {
            moveFromFragment(activity!!, CategoryDetail::class.java)
        }
        homeBinding.seeAllfreshHerbs.setOnClickListener {
            moveFromFragment(activity!!, CategoryDetail::class.java)

        }
        homeBinding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(activity!!, NotificationActivity::class.java)
        }
        homeBinding.toolBar.profileBtn.setOnClickListener {
            moveFromFragment(activity!!, MenuActivity::class.java)
        }

    }

    private fun setViews() {
        setAllAdapters()
    }

    private fun setAllAdapters() {
        homeBinding.batchRv.adapter = BatchAdapterRv(dummyList)
        homeBinding.batchRv.addItemDecoration(HorizantalDivider())
        homeBinding.topSliderVp.adapter = HomeSliderViewPagerAdapter(dummyList)
        homeBinding.topSliderVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        homeBinding.topSliderIndicator.setViewPager2(homeBinding.topSliderVp)
        homeBinding.categoryRv.adapter = CategoryAdapterRv(dummyList)
        homeBinding.categoryRv.addItemDecoration(HorizantalDivider())

        homeBinding.fruitRv.adapter = ItemAdapterRv(dummyList)
        homeBinding.fruitRv.addItemDecoration(HorizantalDivider())

        homeBinding.vegetableRv.adapter = ItemAdapterRv(dummyList)
        homeBinding.vegetableRv.addItemDecoration(HorizantalDivider())

        homeBinding.freshHerbsRv.adapter = ItemAdapterRv(dummyList)
        homeBinding.freshHerbsRv.addItemDecoration(HorizantalDivider())

    }

    private fun setDummyData() {
        dummyList.clear()
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")

    }


}