package com.example.naturescart.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.naturescart.R
import com.example.naturescart.adapters.*
import com.example.naturescart.databinding.FragmentHomeBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.*
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.example.naturescart.ui.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeFragment : Fragment(), Results {

    private lateinit var homeBinding: FragmentHomeBinding
    private val collectionRequest: Int = 1132
    private val loadMoreCollectionRC: Int = 1332
    private val categoriesRequest: Int = 2220
    private val frequentlyProductRequest: Int = 5154
    private val newArrivalProductRequest: Int = 5254
    private val bannerRequest: Int = 5222

    private var limit: Int = 20
    private lateinit var handler: Handler
    private var collectionData: ArrayList<CollectionModel> = ArrayList()
    private var categoriesData: ArrayList<Category> = ArrayList()
    private var frequentlyPurchasedProducts: ArrayList<Product> = ArrayList()
    private var newArrivalProducts: ArrayList<Product> = ArrayList()
    private val layoutManager = LinearLayoutManager(activity)
    private var categoryProductData: ArrayList<CategoryProducts> = ArrayList()
    private var bannerDataLoad: ArrayList<Banner> = ArrayList()

    private var loggedUser: User? = null
    private var isLoading: Boolean = true
    private var isLast: Boolean = false
    private lateinit var paginationListener: PaginationListeners
    private var pageNo: Int = 1
    private lateinit var adapterCollection: CollectionAdapterRv
    private lateinit var layoutManagerNewArrival: GridLayoutManager

    private var loadingView: LoadingDialog? = null
    private lateinit var adapterNewArrivalProducts: ItemAdapterFNRv
    private var animationShown = false
    private val collectionsDataPersistenceKey = "collectionsDataPersistenceKey"
    private val categoriesDataPersistenceKey = "categoriesDataPersistenceKey"
    private val newArrivalDataPersistenceKey = "newArrivalDataPersistenceKey"
    private val frequentlyPurchasedDataPersistenceKey = "frequentlyPurchasedDataPersistenceKey"
    private val bannerDataPersistenceKey = "bannerDataPersistenceKey"


    private var runnable: Runnable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = LoadingDialog(requireContext())
        setAllAdapters()
        setListeners()
        Handler(Looper.getMainLooper()).postDelayed({ getData() }, 500)
    }

    private fun getData() {
        val collectionsData = Persister.with(requireContext()).getPersisted(collectionsDataPersistenceKey, null)
        val categoriesData = Persister.with(requireContext()).getPersisted(categoriesDataPersistenceKey, null)
        val newArrivalData = Persister.with(requireContext()).getPersisted(newArrivalDataPersistenceKey, null)
        val bannerPersistData = Persister.with(requireContext()).getPersisted(bannerDataPersistenceKey, null)


        var frequentlyPurchasedData: String? = ""
        if (loggedUser != null)
            frequentlyPurchasedData = Persister.with(requireContext()).getPersisted(frequentlyPurchasedDataPersistenceKey, null)


        if (collectionsData == null || categoriesData == null || newArrivalData == null || frequentlyPurchasedData == null || bannerPersistData == null)
            loadingView?.show()

        if (bannerPersistData != null)
            onSuccess(bannerRequest, bannerPersistData)
        if (collectionsData != null)
            onSuccess(collectionRequest, collectionsData)
        if (categoriesData != null)
            onSuccess(categoriesRequest, categoriesData)
        if (newArrivalData != null)
            onSuccess(newArrivalProductRequest, newArrivalData)
        if (loggedUser != null && frequentlyPurchasedData != null)
            onSuccess(frequentlyProductRequest, frequentlyPurchasedData)

        if (loggedUser != null)
            DataService(frequentlyProductRequest, this).getFrequentlyPurchasedProducts(loggedUser!!.accessToken)



        DataService(bannerRequest, this).getBanner()
        DataService(collectionRequest, this).getCollections(pageNo, limit)
        DataService(categoriesRequest, this).getCategories(limit, false)
        DataService(newArrivalProductRequest, this).getNewArrivalProducts()
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
            EventBus.getDefault().postSticky(MoveFragmentEvent(SearchFragment()))
        }
    }

    private fun setAllAdapters() {
        adapterCollection = CollectionAdapterRv(collectionData) { collection -> onCollectionClicked(collection) }
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        homeBinding.collectionRv.layoutManager = layoutManager
        homeBinding.collectionRv.adapter = adapterCollection
        homeBinding.collectionRv.addItemDecoration(HorizantalDivider())

        homeBinding.topSliderVp.adapter = HomeSliderViewPagerAdapter(bannerDataLoad)
        homeBinding.topSliderVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        homeBinding.topSliderIndicator.setViewPager2(homeBinding.topSliderVp)
        setTopSliderAnimator()

        homeBinding.categoryRv.adapter = CategoryAdapterRv(categoriesData) { categoryId, categoryName -> seeAll(categoryId, categoryName) }
        homeBinding.categoryRv.addItemDecoration(HorizantalDivider())

        homeBinding.frequentlyPurchasedRv.adapter = ItemAdapterFNRv(requireActivity(), frequentlyPurchasedProducts) { item -> onProductDetail(item) }
        homeBinding.frequentlyPurchasedRv.addItemDecoration(HorizantalDivider())
        adapterNewArrivalProducts = ItemAdapterFNRv(requireActivity(), newArrivalProducts) { item -> onProductDetail(item) }
        layoutManagerNewArrival = GridLayoutManager(requireContext(), 2)
        homeBinding.newArrivalRv.layoutManager = layoutManagerNewArrival
        homeBinding.newArrivalRv.adapter = adapterNewArrivalProducts
        homeBinding.newArrivalRv.addItemDecoration(HorizantalDoubleDivider())
    }

    private fun onProductDetail(item: Product) {
        EventBus.getDefault().postSticky(MoveFragmentEvent(ProductDetailsFragment(item)))
    }

    private fun setTopSliderAnimator() {
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            homeBinding.topSliderVp.setCurrentItem((homeBinding.topSliderVp.currentItem + 1) % (homeBinding.topSliderVp.adapter?.itemCount ?: 0), true)
        }
        handler.postDelayed({ runnable }, 3000)
    }

    private fun seeAll(categoryId: Long, categoryName: String) {
        EventBus.getDefault().postSticky(MoveFragmentEvent(CategoryDetailFragment(categoryId, categoryName)))
//        activity?.startActivityForResult(CategoryDetailActivity.newInstance(requireActivity(), categoryId, categoryName), Constants.categoryDetailsActivityRc)
    }

    private fun onCollectionClicked(collection: CollectionModel) {
        EventBus.getDefault().postSticky(MoveFragmentEvent(CollectionDetailFragment(collection.id, collection.name)))
//        activity?.startActivityForResult(CollectionDetailActivity.newInstance(requireActivity(), collection.id, collection.name), Constants.collectionDetailsActivityRc)
    }

    override fun onResume() {
        super.onResume()
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
        if (loggedUser != null)
            DataService(frequentlyProductRequest, this).getFrequentlyPurchasedProducts(loggedUser!!.accessToken)
        else {
            homeBinding.frequentlyPurchasedRv.visibility = View.GONE
            homeBinding.frequentlyPurchasedLabel.visibility = View.INVISIBLE
        }
        notifyAdapters()
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
                categoriesData.clear()
                categoriesData.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<Category>>() {}.type))
                showData()
                Persister.with(requireContext()).persist(categoriesDataPersistenceKey, data)
            }
            newArrivalProductRequest -> {
                newArrivalProducts.clear()
                newArrivalProducts.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<Product>>() {}.type))
                showData()
                Persister.with(requireContext()).persist(newArrivalDataPersistenceKey, data)
            }
            frequentlyProductRequest -> {
                frequentlyPurchasedProducts.clear()
                frequentlyPurchasedProducts.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<Product>>() {}.type))
                showData()
                Persister.with(requireContext()).persist(frequentlyPurchasedDataPersistenceKey, data)
            }
            bannerRequest -> {
                bannerDataLoad.clear()
                bannerDataLoad.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<Banner>>() {}.type))
                showData()
                Persister.with(requireContext()).persist(bannerDataPersistenceKey, data)

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
        if (!collectionData.isNullOrEmpty() && !categoriesData.isNullOrEmpty() && !newArrivalProducts.isNullOrEmpty() && !bannerDataLoad.isNullOrEmpty()) {
            if (loggedUser != null && !frequentlyPurchasedProducts.isNullOrEmpty()) {
                showDataLoggedUser()
                if (frequentlyPurchasedProducts.size < 1) {
                    homeBinding.frequentlyPurchasedRv.visibility = View.GONE
                    homeBinding.frequentlyPurchasedLabel.visibility = View.GONE
                } else {
                    homeBinding.frequentlyPurchasedRv.visibility = View.VISIBLE
                    homeBinding.frequentlyPurchasedLabel.visibility = View.VISIBLE
                }
                homeBinding.frequentlyPurchasedRv.adapter?.notifyDataSetChanged()
            }
            if (loggedUser == null) {
                showDataLoggedUser()
            }
        }
    }

    private fun showDataLoggedUser() {
        loadingView?.dismiss()
        //show collections data
        homeBinding.collectionRv.adapter?.notifyDataSetChanged()
        homeBinding.topSliderVp.adapter?.notifyDataSetChanged()
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
        homeBinding.newArrivalRv.adapter?.notifyDataSetChanged()
        animationShown = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        if (runnable != null)
            handler.removeCallbacks(runnable!!)
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartUpdated(event: CartUpdateEvent) {
//        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun notifyAdapters() {
        homeBinding.newArrivalRv.adapter?.notifyDataSetChanged()
        if (loggedUser != null)
            homeBinding.frequentlyPurchasedRv.adapter?.notifyDataSetChanged()
    }

}