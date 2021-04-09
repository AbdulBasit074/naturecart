package com.example.naturescart.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.naturescart.R
import com.example.naturescart.adapters.FavouriteViewPagerAdapter
import com.example.naturescart.databinding.FragmentFavouriteBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.Category
import com.example.naturescart.model.Product
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.example.naturescart.ui.UserDetailActivity
import com.google.android.material.tabs.TabLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private var list: ArrayList<String> = ArrayList()
    private var loggedUser: User? = null
    private val loginRequest: Int = 5103
    private var allCategoryList: ArrayList<Category> = ArrayList()
    private lateinit var allProductList: ArrayList<Product>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
        getVisitorFavourite()
        setListeners()
    }

    private fun getVisitorFavourite() {
        allCategoryList.clear()
        binding.tabLayout.removeAllTabs()
        allCategoryList.add(Category())
        allProductList = NatureDb.getInstance(requireActivity()).favouriteDao().getAllProduct() as ArrayList<Product>
        allProductList.forEach {
            if (!allCategoryList.containsWithThisName(it.categoryName ?: ""))
                allCategoryList.add(Category(it.categoryID, 0, it.categoryName, "", ""))
        }
        checkForEmpty()
        tabLayoutSetting()
    }

    private fun checkForEmpty() {
        if (allProductList.isNullOrEmpty()) {
            binding.noFavoritesContainer.visibility = View.VISIBLE
            binding.tabLayout.visibility = View.GONE
        } else {
            binding.noFavoritesContainer.visibility = View.GONE
            binding.tabLayout.visibility = View.VISIBLE
        }
    }


    private fun setListeners() {
        binding.toolBar.appLogo.setOnClickListener {
            EventBus.getDefault().postSticky(LogoClickEvent())
        }

        binding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(requireActivity(), NotificationActivity::class.java)
        }
        binding.toolBar.profileBtn.setOnClickListener {
            if (loggedUser == null)
                moveForResultFragment(requireActivity(), MenuActivity::class.java, loginRequest)
            else
                moveFromFragment(requireActivity(), UserDetailActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
    }


    private fun tabLayoutSetting() {
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        allCategoryList.forEach {
            if (it.name == "All")
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.all)).setTag(it.id))
            else
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.name).setTag(it.id))
        }

        binding.viewPager.adapter = FavouriteViewPagerAdapter(requireActivity(), childFragmentManager, lifecycle, allCategoryList.size, allCategoryList)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.getTabAt(position)!!.select()
            }
        })
        binding.viewPager.currentItem = 0

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }
        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoritesUpdated(event: FavoritesUpdatedEvent) {
        val allProducts = NatureDb.getInstance(requireActivity()).favouriteDao().getAllProduct() as ArrayList<Product>
        val allCategories: ArrayList<Category> = ArrayList()
        allProducts.forEach {
            if (!allCategories.containsWithThisName(it.categoryName ?: ""))
                allCategories.add(Category(it.categoryID, 0, it.categoryName, "", ""))
        }
        if (allCategories.size != (allCategoryList.size - 1))
            getVisitorFavourite()
    }
}