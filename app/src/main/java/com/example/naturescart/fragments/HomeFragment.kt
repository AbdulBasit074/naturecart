package com.example.naturescart.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import com.example.naturescart.helper.*
import com.example.naturescart.model.Category
import com.example.naturescart.model.CategoryProducts
import com.example.naturescart.model.CollectionModel
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.example.naturescart.ui.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment(), Results {

    private lateinit var homeBinding: FragmentHomeBinding
    private val collectionRequest: Int = 1132
    private val loadMoreCollectionRC: Int = 1332
    private val categoriesRequest: Int = 2220
    private val categoriesProductRequest: Int = 5554
    private var limit: Int = 6
    private lateinit var handler:Handler
    private var onDestroyFragment: Boolean = false
    private var collectionData: ArrayList<CollectionModel> = ArrayList()
    private var categoriesData: ArrayList<Category> = ArrayList()
    private val layoutManager = LinearLayoutManager(activity)
    private var categoryProductData: ArrayList<CategoryProducts> = ArrayList()
    private var loggedUser: User? = null
    private var isLoading: Boolean = true
    private var isLast: Boolean = false
    private lateinit var paginationListener: PaginationListeners
    private var pageNo: Int = 1
    private lateinit var adapterCollection: CollectionAdapterRv
    private var loadingView: LoadingDialog? = null
    private var animationShown = false
    private val collectionsDataPersistenceKey = "collectionsDataPersistenceKey"
    private val categoriesDataPersistenceKey = "categoriesDataPersistenceKey"
    private val categoryProductsDataPersistenceKey = "categoryProductsDataPersistenceKey"

    companion object {
        var dummyList: ArrayList<String> = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingView = LoadingDialog(requireContext())
        setViews()
        setDummyData()
        setListeners()
        Handler(Looper.getMainLooper()).postDelayed({ getData() }, 500)
    }

    private fun getData() {
        val collectionsData = Persister.with(requireContext()).getPersisted(collectionsDataPersistenceKey, null)
        val categoriesData = Persister.with(requireContext()).getPersisted(categoriesDataPersistenceKey, null)
        val categoryProductsData = Persister.with(requireContext()).getPersisted(categoryProductsDataPersistenceKey, null)
        if (collectionsData == null || categoriesData == null || categoryProductsData == null)
            loadingView?.show()
        if (collectionsData != null)
            onSuccess(collectionRequest, collectionsData)
        if (categoriesData != null)
            onSuccess(categoriesRequest, categoriesData)
        if (categoryProductsData != null)
            onSuccess(categoriesProductRequest, categoryProductsData)
        DataService(collectionRequest, this).getCollections(pageNo, limit)
        DataService(categoriesRequest, this).getCategories(limit, false)
        DataService(categoriesProductRequest, this).getCategoryProducts(limit, true)
    }

    private fun setListeners() {
        homeBinding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(requireActivity(), NotificationActivity::class.java)
        }
        homeBinding.toolBar.profileBtn.setOnClickListener {
            if (loggedUser == null)
                moveFromFragment(requireActivity(), MenuActivity::class.java)
            else
                moveFromFragment(requireActivity(), UserDetailActivity::class.java)
        }
        homeBinding.searchEt.setOnClickListener {
            moveFromFragment(requireActivity(), SearchActivity::class.java)
        }

    }

    private fun setViews() {
        setAllAdapters()
    }

    private fun setAllAdapters() {
        adapterCollection = CollectionAdapterRv(collectionData) { collection -> onCollectionClicked(collection) }
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        homeBinding.collectionRv.layoutManager = layoutManager
        homeBinding.collectionRv.adapter = adapterCollection
        homeBinding.collectionRv.addItemDecoration(HorizantalDivider())

        homeBinding.topSliderVp.adapter = HomeSliderViewPagerAdapter(dummyList)
        homeBinding.topSliderVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        homeBinding.topSliderIndicator.setViewPager2(homeBinding.topSliderVp)
        setTopSliderAnimator()

        homeBinding.categoryRv.adapter = CategoryAdapterRv(categoriesData) { categoryId, categoryName -> seeAll(categoryId, categoryName) }
        homeBinding.categoryRv.addItemDecoration(HorizantalDivider())

        homeBinding.categoryProductRvDetail.adapter = CategoryDetailProductsRvAdapter(requireActivity(), categoryProductData) { categoryId, categoryName -> seeAll(categoryId, categoryName) }
    }

    private fun setTopSliderAnimator() {
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            homeBinding.topSliderVp.setCurrentItem((homeBinding.topSliderVp.currentItem + 1) % (homeBinding.topSliderVp.adapter?.itemCount ?: 0), true)
            if (!onDestroyFragment)
                setTopSliderAnimator()
        }, 3000)

    }

    private fun seeAll(categoryId: Long, categoryName: String) {
        moveFromFragment(CategoryDetailActivity.newInstance(requireActivity(), categoryId, categoryName))
    }

    private fun onCollectionClicked(collection: CollectionModel) {
        moveFromFragment(CollectionDetailActivity.newInstance(requireActivity(), collection.id, collection.name))
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
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            collectionRequest -> {
                isLoading = false
                collectionData.clear()
                collectionData.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<CollectionModel>>() {}.type))
                if (collectionData.size < PaginationListeners.pageSize)
                    isLast = true
                initPageListener()
                homeBinding.collectionRv.addOnScrollListener(paginationListener)
                showData()
                Persister.with(requireContext()).persist(collectionsDataPersistenceKey, data)
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
                categoriesData.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<Category>>() {}.type))
                showData()
                Persister.with(requireContext()).persist(categoriesDataPersistenceKey, data)
            }
            categoriesProductRequest -> {
                categoryProductData.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<CategoryProducts>>() {}.type))
                showData()
                Persister.with(requireContext()).persist(categoryProductsDataPersistenceKey, data)
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

    private fun showData() {
        if (!collectionData.isNullOrEmpty() && !categoriesData.isNullOrEmpty() && !categoryProductData.isNullOrEmpty()) {
            loadingView?.dismiss()
            //show collections data
            homeBinding.collectionRv.adapter?.notifyDataSetChanged()
            if (!animationShown) {
                homeBinding.collectionRv.scheduleLayoutAnimation()
                homeBinding.topSliderVp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_up))
                homeBinding.topSliderIndicator.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_up))
                homeBinding.topSliderVp.alpha = 1f
                homeBinding.topSliderIndicator.alpha = 1f
            }
            //show categories data
            homeBinding.categoryRv.adapter?.notifyDataSetChanged()
            if (!animationShown)
                homeBinding.categoryRv.scheduleLayoutAnimation()
            //show category products data
            homeBinding.categoryProductRvDetail.adapter?.notifyDataSetChanged()
            animationShown = true
        }
    }

    override fun onDetach() {
        handler.removeCallbacksAndMessages(null)
        super.onDetach()
    }
    override fun onDestroy() {
        super.onDestroy()
    }

}