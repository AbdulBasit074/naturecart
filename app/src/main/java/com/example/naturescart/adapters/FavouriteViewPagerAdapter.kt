package com.example.naturescart.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.naturescart.fragments.CategoryListingFragment
import com.example.naturescart.fragments.FavouriteListingFragment
import com.example.naturescart.model.Category
import com.example.naturescart.model.CategoryDetail

class FavouriteViewPagerAdapter(
    private val activity: Activity, fm: FragmentManager,lifecycle: Lifecycle,
    private val totalTabs: Int,
    private val categoryList: ArrayList<Category>
) : FragmentStateAdapter(fm,lifecycle) {


    override fun getItemCount(): Int {
        return totalTabs
    }

    override fun createFragment(position: Int): Fragment {
        return FavouriteListingFragment(categoryList[position].id!!, categoryList[position].name ?: "")
    }
}