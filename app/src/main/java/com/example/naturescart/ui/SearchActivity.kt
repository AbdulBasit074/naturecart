package com.example.naturescart.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.naturescart.R
import com.example.naturescart.adapters.ItemAdapterRv
import com.example.naturescart.adapters.SearchAdapterRv
import com.example.naturescart.databinding.ActivitySearchBinding
import com.example.naturescart.helper.CartItemAddedEvent
import com.example.naturescart.helper.HorizantalDoubleDivider
import com.example.naturescart.helper.PaginationListeners
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.Product
import com.example.naturescart.model.SearchHistory
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.lang.StringBuilder


class SearchActivity : AppCompatActivity(), Results {

    private lateinit var binding: ActivitySearchBinding
    private val searchHistoryRequest: Int = 2203
    private val searchRequest: Int = 1203
    private val loadMoreRequest: Int = 5203
    private var searchProductList: ArrayList<Product> = ArrayList()
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private lateinit var paginationListener: PaginationListeners
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapterProduct: ItemAdapterRv
    private var pageNo: Int = 1
    private var loggedUser: User? = null
    private var searchList: ArrayList<SearchHistory> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        if (loggedUser != null) {
            DataService(searchHistoryRequest, this).getUserHistory(loggedUser!!.accessToken)
        }
        setAdapterForProducts()
        setSearchEtListener()
        binding.itemAddedDialog.setOnClickListener {
            setResult(RESULT_OK)
            onBackPressed()
        }
    }

    private fun setAdapterForProducts() {
        adapterProduct = ItemAdapterRv(this, searchProductList, "")
        layoutManager = GridLayoutManager(this, 2)
        binding.searchResultRv.layoutManager = layoutManager
        binding.searchResultRv.adapter = adapterProduct
        binding.searchResultRv.addItemDecoration(HorizantalDoubleDivider())
    }

    private var runnable: Runnable? = null
    private var handler: Handler? = null
    private fun setSearchEtListener() {
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            performSearch()
            binding.searchHistoryRv.visibility = View.GONE
        }
        binding.searchEt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProductList.clear()
                performSearch()
                binding.searchEt.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    searchProductList.clear()
                    binding.searchResultRv.adapter?.notifyDataSetChanged()
                    binding.noResultsContainer.visibility = View.VISIBLE
                    binding.searchHistoryRv.visibility = if (searchList.isNullOrEmpty()) View.GONE else View.VISIBLE
                } else {
                    handler?.removeCallbacks(runnable!!)
                    handler?.postDelayed(runnable!!, 2000)
                }
            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun performSearch() {
        binding.searchEt.clearFocus()
        isLoading = true
        pageNo = 1
        if (loggedUser != null) {
            val authToken = loggedUser!!.accessToken
            DataService(searchRequest, this).getSearchResult(
                "Bearer $authToken",
                binding.searchEt.text.toString(),
                PaginationListeners.pageSize, pageNo
            )
        } else {
            DataService(searchRequest, this).getSearchResult(
                null,
                binding.searchEt.text.toString(),
                PaginationListeners.pageSize, pageNo
            )
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            searchHistoryRequest -> {
                searchList = Gson().fromJson(data, object : TypeToken<ArrayList<SearchHistory>>() {}.type)
                setAdapter()
                binding.searchHistoryRv.visibility = if (searchList.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            searchRequest -> {
                adapterProduct.stopLoading()
                isLoading = false
                searchProductList.clear()
                searchProductList.addAll(
                    Gson().fromJson(
                        data,
                        object : TypeToken<ArrayList<Product>>() {}.type
                    )
                )
                if (searchProductList.size < PaginationListeners.pageSize)
                    isLastPage = true
                initPageListener()
                binding.searchResultRv.addOnScrollListener(paginationListener)
                binding.searchResultRv.adapter?.notifyDataSetChanged()
                binding.noResultsContainer.visibility = if (searchProductList.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
            loadMoreRequest -> {
                adapterProduct.stopLoading()
                isLoading = false
                val productList: ArrayList<Product> =
                    Gson().fromJson(data, object : TypeToken<ArrayList<Product>>() {}.type)
                if (productList.size < PaginationListeners.pageSize)
                    isLastPage = true

                searchProductList.addAll(productList)
                adapterProduct.notifyDataSetChanged()
            }
        }
    }

    private fun initPageListener() {
        paginationListener = object : PaginationListeners(layoutManager) {
            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun loadMoreItems() {
                isLoading = true
                pageNo++
                if (loggedUser != null) {
                    val authToken = loggedUser!!.accessToken
                    DataService(searchRequest, this@SearchActivity).getSearchResult(
                        "Bearer $authToken",
                        binding.searchEt.text.toString(),
                        pageSize, pageNo
                    )
                } else {
                    DataService(loadMoreRequest, this@SearchActivity).getSearchResult(
                        null,
                        binding.searchEt.text.toString(),
                        pageSize, pageNo
                    )
                }
                adapterProduct.startLoading()
            }
        }
    }

    private fun setAdapter() {
        binding.searchHistoryRv.adapter = SearchAdapterRv(searchList) { data -> onHistoryClick(data) }
    }

    private fun onHistoryClick(data: String) {
        binding.searchEt.text.clear()
        binding.searchEt.setText(data)
        performSearch()
    }

    override fun onFailure(requestCode: Int, data: String) {
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