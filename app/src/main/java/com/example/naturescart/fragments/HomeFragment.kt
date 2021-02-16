package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.naturescart.R
import com.example.naturescart.adapters.CategoryAdapterRv
import com.example.naturescart.adapters.CategoryDetailProductsRvAdapter
import com.example.naturescart.adapters.CollectionAdapterRv
import com.example.naturescart.adapters.HomeSliderViewPagerAdapter
import com.example.naturescart.databinding.FragmentHomeBinding
import com.example.naturescart.helper.HorizantalDivider
import com.example.naturescart.helper.PaginationListeners
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.Category
import com.example.naturescart.model.CategoryProducts
import com.example.naturescart.model.CollectionModel
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.example.naturescart.ui.SearchActivity
import com.example.naturescart.ui.UserDetailActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class HomeFragment : Fragment(), Results {

    companion object {
        var dummyList: ArrayList<String> = ArrayList()
    }


    private lateinit var homeBinding: FragmentHomeBinding
    private val collectionRequest: Int = 1132
    private val loadMoreCollectionRC: Int = 1332
    private val categoriesRequest: Int = 2220
    private val categoriesProductRequest: Int = 5554
    private var limit: Int = 6
    private var collectionData: ArrayList<CollectionModel> = ArrayList()
    private var categoriesData: ArrayList<Category> = ArrayList()
    private val layoutManager = LinearLayoutManager(activity)
    private var categoryProductData: ArrayList<CategoryProducts> = ArrayList()
    private var loggedUser: User? = null
    private var isLoading: Boolean = true
    private var isLast: Boolean = false
    private lateinit var paginationListener: PaginationListeners
    private var loadMore: Boolean = false
    private var pageNo: Int = 1
    private lateinit var adapterCollection: CollectionAdapterRv


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        loggedUser = NatureDb.newInstance(activity!!).userDao().getLoggedUser()
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        getData()
        setDummyData()
        setListeners()
    }

    private fun getData() {
        DataService(collectionRequest, this).getCollections(pageNo, limit)
        DataService(categoriesRequest, this).getCategories(limit, false)
        DataService(categoriesProductRequest, this).getCategoryProducts(limit, true)
    }

    private fun setListeners() {

        homeBinding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(activity!!, NotificationActivity::class.java)
        }
        homeBinding.toolBar.profileBtn.setOnClickListener {
            if (loggedUser == null)
                moveFromFragment(activity!!, MenuActivity::class.java)
            else
                moveFromFragment(activity!!, UserDetailActivity::class.java)
        }
        homeBinding.searchEt.setOnClickListener {
            moveFromFragment(activity!!, SearchActivity::class.java)
        }

    }

    private fun setViews() {
        setAllAdapters()
    }

    private fun setAllAdapters() {

        adapterCollection = CollectionAdapterRv(collectionData)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        homeBinding.collectionRv.layoutManager = layoutManager
        homeBinding.collectionRv.adapter = adapterCollection
        homeBinding.collectionRv.addItemDecoration(HorizantalDivider())


        homeBinding.topSliderVp.adapter = HomeSliderViewPagerAdapter(dummyList)
        homeBinding.topSliderVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        homeBinding.topSliderIndicator.setViewPager2(homeBinding.topSliderVp)

        homeBinding.categoryRv.adapter = CategoryAdapterRv(categoriesData)
        homeBinding.categoryRv.addItemDecoration(HorizantalDivider())


        homeBinding.categoryProductRvDetail.adapter =
            CategoryDetailProductsRvAdapter(
                activity!!,
                categoryProductData
            ) { forLogin() }
    }

    private fun forLogin() {
        moveFromFragment(activity!!, MenuActivity::class.java)
    }

    private fun setDummyData() {
        dummyList.clear()
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")
        dummyList.add("Dummy Data")

    }

    override fun onResume() {
        super.onResume()
        loggedUser = NatureDb.newInstance(activity!!).userDao().getLoggedUser()
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            collectionRequest -> {
                isLoading = false
                collectionData.clear()
                collectionData.addAll(
                    Gson().fromJson(
                        data,
                        object : TypeToken<ArrayList<CollectionModel>>() {}.type
                    )
                )
                if (collectionData.size < PaginationListeners.pageSize)
                    isLast = true
                initPageListener()
                homeBinding.collectionRv.addOnScrollListener(paginationListener)
                homeBinding.collectionRv.adapter?.notifyDataSetChanged()
            }
            loadMoreCollectionRC -> {
                adapterCollection.stopLoading()
                isLoading = false
                val dataList: ArrayList<CollectionModel> = Gson().fromJson(data, object : TypeToken<ArrayList<CollectionModel>>() {}.type)
                if (dataList.size < PaginationListeners.pageSize)
                    isLast = true
                collectionData.addAll(dataList)
                homeBinding.collectionRv.adapter?.notifyDataSetChanged()
            }

            categoriesRequest -> {
                categoriesData.addAll(
                    Gson().fromJson(
                        data,
                        object : TypeToken<ArrayList<Category>>() {}.type
                    )
                )
                homeBinding.categoryRv.adapter?.notifyDataSetChanged()
            }
            categoriesProductRequest -> {
                categoryProductData.addAll(
                    Gson().fromJson(
                        data,
                        object : TypeToken<ArrayList<CategoryProducts>>() {}.type
                    )
                )
                homeBinding.categoryProductRvDetail.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }

    private fun initPageListener() {
        paginationListener = object : PaginationListeners(layoutManager) {
            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun isLastPage(): Boolean {
                return isLast
            }
            override fun loadMoreItems() {
                pageNo++
                isLoading = true
                DataService(loadMoreCollectionRC, this@HomeFragment).getCollections(pageNo, limit)
                adapterCollection.startLoading()
            }
        }


    }

}