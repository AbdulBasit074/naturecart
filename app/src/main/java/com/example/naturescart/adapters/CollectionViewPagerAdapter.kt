package com.example.naturescart.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.naturescart.fragments.CollectionListingFragment

class CollectionViewPagerAdapter(fm: FragmentManager, private val totalTabs: Int, private val categoryNames: ArrayList<String>, private val collectionId: Long, private val collectionName: String) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {



    override fun getItem(position: Int): Fragment {
        return CollectionListingFragment(categoryNames[position], collectionId, collectionName)
    }
    override fun getCount(): Int {
        return totalTabs
    }
}