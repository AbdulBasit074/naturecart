package com.example.naturescart.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.adapters.CollectionViewPagerAdapter
import com.example.naturescart.databinding.ActivityCollectionDetailBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.LoadingDialog
import com.example.naturescart.helper.PaginationListeners
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.CollectionModel
import com.example.naturescart.model.Product
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson

class CollectionDetailActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, Results {

    private var categoryDetailRequest: Int = 1122
    private lateinit var binding: ActivityCollectionDetailBinding
    private var list: ArrayList<Product> = ArrayList()
    private var collectionId: Long = 0
    private var collectionName: String = ""
    private var loadingView: LoadingDialog? = null

    companion object {
        fun newInstance(context: Context, collectionId: Long?, collectionName: String): Intent {
            val intent = Intent(context, CollectionDetailActivity::class.java)
            intent.putExtra(Constants.categoryID, collectionId)
            intent.putExtra(Constants.categoryName, collectionName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collection_detail)
        loadingView = LoadingDialog(this)
        loadingView?.show()
        collectionId = intent.getLongExtra(Constants.categoryID, 0)
        collectionName = intent.getStringExtra(Constants.categoryName) ?: ""
        DataService(categoryDetailRequest, this).getCollectionProducts(collectionId, 1, PaginationListeners.pageSize)
        setListeners()
    }

    private fun setListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }


    private fun tabLayoutSetting() {

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val categoryNames: ArrayList<String> = ArrayList()
        list.forEach {
            if (it.name == null) {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.all)).setTag(getString(R.string.all)))
                categoryNames.add(getString(R.string.all))
            } else {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.categoryName).setTag(it.categoryName))
                categoryNames.add(it.categoryName ?: "")
            }
        }
        binding.viewPager.adapter = CollectionViewPagerAdapter(supportFragmentManager, list.size, categoryNames, collectionId, collectionName)
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
                val collection: CollectionModel = Gson().fromJson(data, CollectionModel::class.java)
                binding.actionBarTitleTv.text = collection.name
                list.add(Product())
                list.addAll(collection.products)
                tabLayoutSetting()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        loadingView?.dismiss()
        showToast(data)
    }
}