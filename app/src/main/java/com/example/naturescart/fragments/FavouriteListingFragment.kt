package com.example.naturescart.fragments

import android.content.Context
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
import com.example.naturescart.helper.FavoritesUpdatedEvent
import com.example.naturescart.helper.HorizantalDoubleDivider
import com.example.naturescart.helper.LogInEvent
import com.example.naturescart.helper.MoveFragmentEvent
import com.example.naturescart.model.CollectionModel
import com.example.naturescart.model.Product
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.product.ProductService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FavouriteListingFragment(private val categoryID: Long, private val categoryName: String) : Fragment(), Results {

    private lateinit var binding: FragmentCategoryListingBinding
    private var allProductList: ArrayList<Product> = ArrayList()
    private val getFavoritesRc = 2398

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_listing, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        allProductList.clear()
        allProductList = if (categoryID.toInt() == 0)
            NatureDb.getInstance(requireActivity()).favouriteDao().getAllProduct() as ArrayList<Product>
        else
            NatureDb.getInstance(requireActivity()).favouriteDao().getProductByCategoryName(categoryName) as ArrayList<Product>
        binding.productRvDetail.layoutManager = GridLayoutManager(activity, 2)
        if (binding.productRvDetail.itemDecorationCount == 0)
            binding.productRvDetail.addItemDecoration(HorizantalDoubleDivider())
        binding.productRvDetail.adapter = ItemAdapterRv(requireActivity(), allProductList, categoryName) { item -> onProductDetail(item) }
    }

    private fun onProductDetail(item: Product) {
        EventBus.getDefault().postSticky(MoveFragmentEvent(ProductDetailsFragment(item)))
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            getFavoritesRc -> {
                val favoritesDao = NatureDb.getInstance(requireContext()).favouriteDao()
                val favorites: ArrayList<CollectionModel> = Gson().fromJson(data, object : TypeToken<ArrayList<CollectionModel>>() {}.type)
                favorites.forEach {
                    it.products.forEach { product ->
                        favoritesDao.insertProduct(product)
                    }
                }
                setData()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserLoggedIn(event: LogInEvent) {
        val loggedUser = NatureDb.getInstance(requireContext()).userDao().getLoggedUser()
        ProductService(getFavoritesRc, this).getFavorites(loggedUser!!.accessToken)
        setData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoritesUpdated(event: FavoritesUpdatedEvent) {
        setData()
    }


}