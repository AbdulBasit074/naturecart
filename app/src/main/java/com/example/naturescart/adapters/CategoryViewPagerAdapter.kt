package com.example.naturescart.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.naturescart.fragments.CategoryListingFragment
import com.example.naturescart.model.CategoryDetail

class CategoryViewPagerAdapter(
    private val activity: Activity, fm: FragmentManager,lifeCycle:Lifecycle,
    private val totalTabs: Int,
    private val categoryList: ArrayList<CategoryDetail.Child>,
    private val categoryID: Long,
    private val categoryName:String
) : FragmentStateAdapter(fm,lifeCycle) {

//    override fun getItem(position: Int): Fragment {
//        return if (position == 0)
//            CategoryListingFragment(position, categoryID, categoryName)
//        else
//            CategoryListingFragment(position, categoryList[position].id!!, categoryName)
//    }
//
//    override fun getCount(): Int {
//        return totalTabs
//    }

    override fun getItemCount(): Int {
        return totalTabs
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            CategoryListingFragment(position, categoryID, categoryName)
        else
            CategoryListingFragment(position, categoryList[position].id!!, categoryName)
    }
}