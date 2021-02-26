package com.example.naturescart.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.naturescart.fragments.CategoryListingFragment
import com.example.naturescart.model.CategoryDetail

class CategoryViewPagerAdapter(
    private val activity: Activity, fm: FragmentManager,
    private val totalTabs: Int,
    private val categoryList:ArrayList<CategoryDetail.Child>,
    private val categoryID:Long
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return if(position==0)
            CategoryListingFragment(position,categoryID)
        else
            CategoryListingFragment(position,categoryList[position].id!!)
    }

    override fun getCount(): Int {
        return totalTabs
    }
}