package com.example.naturescart.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.naturescart.fragments.CategoryListingFragment
import com.example.naturescart.fragments.FavouriteListingFragment
import com.example.naturescart.model.Category
import com.example.naturescart.model.CategoryDetail

class FavouriteViewPagerAdapter(
    private val activity: Activity, fm: FragmentManager,
    private val totalTabs: Int,
    private val categoryList:ArrayList<Category>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return FavouriteListingFragment(categoryList[position].id!!)
    }

    override fun getCount(): Int {
        return totalTabs
    }
}