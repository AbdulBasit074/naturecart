package com.example.naturescart.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.naturescart.fragments.CollectionListingFragment

class CollectionViewPagerAdapter(fm: FragmentManager,lifecycle: Lifecycle, private val totalTabs: Int, private val categoryNames: ArrayList<String>, private val collectionId: Long, private val collectionName: String) :
    FragmentStateAdapter(fm,lifecycle) {

    override fun getItemCount(): Int {
        return totalTabs
    }
    override fun createFragment(position: Int): Fragment {
        return CollectionListingFragment(categoryNames[position], collectionId, collectionName)
    }
}