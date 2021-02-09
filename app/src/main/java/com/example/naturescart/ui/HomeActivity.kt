package com.example.naturescart.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityHomeBinding
import com.example.naturescart.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {


    private lateinit var currentFragment: Fragment
    private lateinit var binding: ActivityHomeBinding
    private var previous: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        //default fragment home open
//        currentFragment = HomeFragment()

        loadFragment(HomeFragment())
        previous = binding.bottomNavigation.menu.findItem(binding.bottomNavigation.selectedItemId)
        binding.bottomNavigation.itemIconTintList = null
        bottomNavigationFragments()

    }

    private fun loadFragment(fragment: Fragment) {
        val transition: FragmentTransaction = supportFragmentManager.beginTransaction()
        transition.replace(binding.frameContainer.id, fragment)
        transition.addToBackStack(null)
        transition.commit()
    }

    private fun bottomNavigationFragments() {


        val mBottomNavigationListener = BottomNavigationView.OnNavigationItemSelectedListener {

            setPreviousUiUpdate()
            previous = it
            when (it.itemId) {

                R.id.homeNav -> {

                    loadFragment(HomeFragment())
                    it.setIcon(R.drawable.ic_home_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourite -> {

                    loadFragment(FavouriteFragment())
                    it.setIcon(R.drawable.ic_heart_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cart -> {
                    loadFragment(CartFragment())
                    it.setIcon(R.drawable.ic_cart_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.order -> {
                    loadFragment(OrderFragment())
                    it.setIcon(R.drawable.ic_order_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.about -> {
                    loadFragment(AboutFragment())
                    it.setIcon(R.drawable.ic_about_checked)
                    return@OnNavigationItemSelectedListener true
                }
            }

            return@OnNavigationItemSelectedListener false
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener(mBottomNavigationListener)


    }

    private fun setPreviousUiUpdate() {
        if (previous == null)
            return

        when (previous!!.itemId) {

            R.id.homeNav -> {
                previous!!.setIcon(R.drawable.ic_home)
            }
            R.id.favourite -> {

                previous!!.setIcon(R.drawable.ic_heart_white)
            }
            R.id.cart -> {

                previous!!.setIcon(R.drawable.ic_cart_white)
            }
            R.id.order -> {
                previous!!.setIcon(R.drawable.ic_order_white)
            }
            R.id.about -> {
                previous!!.setIcon(R.drawable.ic_about)
            }

        }
    }
}