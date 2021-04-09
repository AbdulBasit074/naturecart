package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.adapters.CategoryViewPagerAdapter
import com.example.naturescart.databinding.ActivityCategoryDetailBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CategoryDetail
import com.example.naturescart.services.Results
import com.example.naturescart.services.category.CategoryService
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.annotations.Until
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CategoryDetailFragment(private val id: Long? = 0, private val name: String = "") : Fragment(), TabLayout.OnTabSelectedListener, Results {

    private lateinit var binding: ActivityCategoryDetailBinding
    private var list: ArrayList<CategoryDetail.Child> = ArrayList()
    private var listItems: ArrayList<CategoryDetail.Child> = ArrayList()
    private var categoryId: Long = 0
    private var categoryName: String = ""
    private var categoryDetail = CategoryDetail()
    private var categoryDetailRequest: Int = 1122

    private var loadingView: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_category_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingView = LoadingDialog(requireContext())
        loadingView?.show()
        categoryId = id!!
        categoryName = name
        CategoryService(categoryDetailRequest, this).getCategory(
            categoryId, PaginationListeners.pageSize,
            withProducts = true,
            isChild = false, pageNo = 1
        )
        setListeners()
    }

    private fun setListeners() {
        binding.itemAddedDialog.setOnClickListener {
            EventBus.getDefault().postSticky(ClickCartItemEvent())
        }
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun tabLayoutSetting() {
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        listItems.clear()

        list.forEach {
            if (it.name == getString(R.string.all))
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.all)).setTag(it.id))
            else
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.name).setTag(it.id))
        }
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.viewPager.adapter = CategoryViewPagerAdapter(requireActivity(), requireActivity().supportFragmentManager, lifecycle, list.size, list, categoryId, categoryName)

        binding.viewPager.currentItem = 0

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.getTabAt(position)!!.select()
            }
        })
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

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        var selectedIndex = 0
        for (i in 0 until list.size) {
            if (list[i].id == tab!!.tag)
                selectedIndex = i
        }
        binding.viewPager.currentItem = selectedIndex
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            categoryDetailRequest -> {
                loadingView?.dismiss()
                categoryDetail = Gson().fromJson(data, CategoryDetail::class.java)
                Glide.with(this).load(categoryDetail.image).into(binding.tabHeader)
                val categoryForAll = CategoryDetail.Child()
                categoryForAll.name = getString(R.string.all)
                list.add(categoryForAll)
                list.addAll(categoryDetail.childs!!)
                tabLayoutSetting()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        loadingView?.dismiss()
        showToast(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartUpdated(event: CartItemAddedEvent) {
        binding.itemsCountTv.text = StringBuilder().append("Total ${if (event.itemCount == 1) "Item" else "Items"}: ").append(event.itemCount)
        binding.totalTv.text = getString(R.string.aed_price, String.format("%.2f", event.total))
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }
}