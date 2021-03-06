package com.example.naturescart.adapters

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.naturescart.fragments.*

class HomeFragmentsVpAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 5
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> FavouriteFragment()
            2 -> CartFragment()
            3 -> OrderFragment()
            4 -> AboutFragment()
            else -> HomeFragment()
        }
    }

}