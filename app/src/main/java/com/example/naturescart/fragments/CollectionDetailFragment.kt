package com.example.naturescart.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.example.naturescart.R
import com.example.naturescart.adapters.CollectionViewPagerAdapter
import com.example.naturescart.databinding.ActivityCollectionDetailBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CollectionModel
import com.example.naturescart.model.Product
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.example.naturescart.ui.CollectionDetailActivity
import com.example.naturescart.ui.HomeActivity
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CollectionDetailFragment(private val id: Long? = 0, private val name: String = "") : Fragment(), ViewPager.OnPageChangeListener, Results {


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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_collection_detail, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingView = LoadingDialog(requireContext())
        loadingView?.show()
        collectionId = id!!
        collectionName = name
        DataService(categoryDetailRequest, this).getCollectionProducts(collectionId, 1, PaginationListeners.pageSize)
        setListeners()
    }
    private fun setListeners() {
        binding.itemAddedDialog.setOnClickListener {
            EventBus.getDefault().postSticky(ClickCartItemEvent())
        }
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
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
        binding.viewPager.adapter = CollectionViewPagerAdapter(requireActivity().supportFragmentManager, list.size, categoryNames, collectionId, collectionName)
        binding.viewPager.addOnPageChangeListener(this@CollectionDetailFragment)
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartUpdated(event: CartItemAddedEvent) {
        binding.itemsCountTv.text = StringBuilder().append("Total ${if (event.itemCount == 1) "Item" else "Items"}: ").append(event.itemCount)
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