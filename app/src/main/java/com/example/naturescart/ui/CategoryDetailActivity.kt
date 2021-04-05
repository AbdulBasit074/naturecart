package com.example.naturescart.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.lang.StringBuilder

class CategoryDetailActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener,
    ViewPager.OnPageChangeListener, Results {

    private lateinit var binding: ActivityCategoryDetailBinding
    private var list: ArrayList<CategoryDetail.Child> = ArrayList()
    private var categoryId: Long = 0
    private var categoryName: String = ""
    private var categoryDetail = CategoryDetail()
    private var loadingView: LoadingDialog? = null

    companion object {
        fun newInstance(context: Context, categoryId: Long?, categoryName: String): Intent {
            val intent = Intent(context, CategoryDetailActivity::class.java)
            intent.putExtra(Constants.categoryID, categoryId)
            intent.putExtra(Constants.categoryName, categoryName)
            return intent
        }
    }

    private var categoryDetailRequest: Int = 1122

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_detail)
        loadingView = LoadingDialog(this)
        loadingView?.show()
        categoryId = intent.getLongExtra(Constants.categoryID, 0)
        categoryName = intent.getStringExtra(Constants.categoryName) ?: ""
        CategoryService(categoryDetailRequest, this).getCategory(
            categoryId, PaginationListeners.pageSize,
            withProducts = true,
            isChild = false, pageNo = 1
        )
        setListeners()

    }

    private fun setListeners() {

        binding.itemAddedDialog.setOnClickListener {
            setResult(RESULT_OK)
            onBackPressed()
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun tabLayoutSetting() {

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        list.forEach {
            if (it.name == Constants.getTranslate(this, "all"))
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(Constants.getTranslate(this, "all")).setTag(it.name)
                )
            else
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(it.name).setTag(it.name)
                )
        }
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.viewPager.adapter = CategoryViewPagerAdapter(this, supportFragmentManager, list.size, list, categoryId, categoryName)
        binding.viewPager.addOnPageChangeListener(this)
        onPageSelected(0)

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
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
            if (list[i].name == tab!!.tag)
                selectedIndex = i
        }
        binding.viewPager.currentItem = selectedIndex
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        binding.tabLayout.getTabAt(position)!!.select()
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            categoryDetailRequest -> {
                loadingView?.dismiss()
                categoryDetail = Gson().fromJson(data, CategoryDetail::class.java)
                        Glide.with(this).load(categoryDetail.image).into(binding.tabHeader)
                list.add(CategoryDetail.Child())
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
        binding.itemsCountTv.text =
            StringBuilder().append(Constants.getTranslate(this, "total") + "${if (event.itemCount == 1) Constants.getTranslate(this, "item") else Constants.getTranslate(this, "items")}: ")
                .append(event.itemCount)
        binding.totalTv.text = getString(R.string.aed_price, String.format("%.2f", event.total))
        binding.itemAddedDialog.visibility = View.VISIBLE
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