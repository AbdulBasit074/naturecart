package com.example.naturescart.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.naturescart.fragments.CategoryListingFragment

class CategoryViewPagerAdapter(
    private val activity: Activity, fm: FragmentManager,
    private val totalTabs: Int
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return CategoryListingFragment()
    }

    override fun getCount(): Int {
        return totalTabs
    }
}