package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.naturescart.R
import com.example.naturescart.adapters.ItemAdapterRv
import com.example.naturescart.databinding.FragmentCollectionListingBinding
import com.example.naturescart.helper.HorizantalDoubleDivider
import com.example.naturescart.helper.PaginationListeners
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.CollectionModel
import com.example.naturescart.model.Product
import com.example.naturescart.services.Results
import com.example.naturescart.services.data.DataService
import com.google.gson.Gson

class CollectionListingFragment(private val categoryName: String, private val collectionId: Long, private val collectionName: String) : Fragment(), Results {

    private lateinit var binding: FragmentCollectionListingBinding
    private var productList: ArrayList<Product> = ArrayList()
    private val loadMoreRequest: Int = 5203
    private val categoryWithRequest: Int = 5003
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = true
    private var pageNo: Int = 1
    private lateinit var paginationListener: PaginationListeners
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapterProduct: ItemAdapterRv

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_collection_listing, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataService(categoryWithRequest, this).getCollectionProducts(collectionId, pageNo, PaginationListeners.pageSize)
        layoutManager = GridLayoutManager(activity, 2)
        binding.productRvDetail.layoutManager = layoutManager
        adapterProduct = ItemAdapterRv(requireActivity(), productList, collectionName)
        binding.productRvDetail.addItemDecoration(HorizantalDoubleDivider())
        binding.productRvDetail.adapter = adapterProduct
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            categoryWithRequest -> {
                isLoading = false
                productList.clear()
                adapterProduct.stopLoading()
                val collection: CollectionModel = Gson().fromJson(data, CollectionModel::class.java)
                collection.products.forEach {
                    if (it.categoryName == categoryName || categoryName == getString(R.string.all))
                        productList.add(it)
                }
                if (productList.size < PaginationListeners.pageSize)
                    isLastPage = true
                initPageListener()
                binding.productRvDetail.addOnScrollListener(paginationListener)
                binding.productRvDetail.adapter?.notifyDataSetChanged()
            }
            loadMoreRequest -> {
                isLoading = false
                adapterProduct.stopLoading()
                val collection: CollectionModel = Gson().fromJson(data, CollectionModel::class.java)
                if (collection.products.size < PaginationListeners.pageSize)
                    isLastPage = true
                collection.products.forEach {
                    if (it.categoryName == categoryName || categoryName == getString(R.string.all))
                        productList.add(it)
                }
                binding.productRvDetail.adapter?.notifyDataSetChanged()
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
                DataService(loadMoreRequest, this@CollectionListingFragment).getCollectionProducts(collectionId, pageNo, pageSize)
                adapterProduct.startLoading()
            }
        }


    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }

}