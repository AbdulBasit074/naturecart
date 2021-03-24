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
import com.example.naturescart.databinding.FragmentCategoryListingBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CategoryDetail
import com.example.naturescart.model.Product
import com.example.naturescart.services.Results
import com.example.naturescart.services.category.CategoryService
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class CategoryListingFragment(private val position: Int, private val categoryID: Long, private val categoryName: String) : Fragment(), Results {

    private lateinit var binding: FragmentCategoryListingBinding
    private var listItems: ArrayList<String> = ArrayList()
    private var productList: ArrayList<Product> = ArrayList()
    private val loadMoreRequest: Int = 5203
    private val categoryWithRequest: Int = 5003
    private val categoryDetailRequest: Int = 1003
    private var categoryDetail: CategoryDetail = CategoryDetail()
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = true
    private var pageNo: Int = 1
    private lateinit var paginationListener: PaginationListeners
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapterProduct: ItemAdapterRv

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_listing, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (position == 0) {
            CategoryService(categoryWithRequest, this).getCategory(
                categoryID,
                PaginationListeners.pageSize,
                withProducts = true,
                isChild = false, pageNo = pageNo
            )

        } else {
            CategoryService(categoryWithRequest, this).getCategory(
                categoryID,
                PaginationListeners.pageSize,
                withProducts = true,
                isChild = true, pageNo = pageNo
            )
        }
        layoutManager = GridLayoutManager(activity, 2)
        binding.productRvDetail.layoutManager = layoutManager
        adapterProduct = ItemAdapterRv(requireActivity(), productList, categoryName) { item -> onProductDetail(item) }
        binding.productRvDetail.addItemDecoration(HorizantalDoubleDivider())
        binding.productRvDetail.adapter = adapterProduct
    }
    private fun onProductDetail(item: Product) {
        EventBus.getDefault().postSticky(MoveFragmentEvent(ProductDetailsFragment(item)))
    }
    override fun onSuccess(requestCode: Int, data: String) {

        when (requestCode) {
            categoryWithRequest -> {
                isLoading = false
                productList.clear()
                adapterProduct.stopLoading()
                categoryDetail = Gson().fromJson(data, CategoryDetail::class.java)
                productList.addAll(categoryDetail.products!!)
                if (productList.size < PaginationListeners.pageSize)
                    isLastPage = true
                initPageListener()
                binding.productRvDetail.addOnScrollListener(paginationListener)
                binding.productRvDetail.adapter?.notifyDataSetChanged()
            }
            loadMoreRequest -> {
                isLoading = false
                adapterProduct.stopLoading()
                val categoryDetailDummy = Gson().fromJson(data, CategoryDetail::class.java)
                if (categoryDetailDummy.products?.size!! < PaginationListeners.pageSize)
                    isLastPage = true
                productList.addAll(categoryDetailDummy.products!!)
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
                CategoryService(loadMoreRequest, this@CategoryListingFragment).getCategory(
                    categoryID,
                    PaginationListeners.pageSize,
                    withProducts = true,
                    isChild = true, pageNo = pageNo
                )
                adapterProduct.startLoading()
            }
        }


    }


    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }

}