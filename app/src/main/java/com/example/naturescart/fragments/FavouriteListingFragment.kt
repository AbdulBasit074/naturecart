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
import com.example.naturescart.helper.HorizantalDoubleDivider
import com.example.naturescart.model.Product
import com.example.naturescart.model.room.NatureDb

class FavouriteListingFragment(private val categoryID: Long) : Fragment() {
    private lateinit var binding: FragmentCategoryListingBinding
    private var allProductList: ArrayList<Product> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_category_listing, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allProductList.clear()
        allProductList = if (categoryID.toInt() == 0)
            NatureDb.newInstance(requireActivity()).favouriteDao()
                .getAllProduct() as ArrayList<Product>
        else
            NatureDb.newInstance(requireActivity()).favouriteDao()
                .getProductByCategory(categoryID) as ArrayList<Product>


        binding.productRvDetail.layoutManager = GridLayoutManager(activity, 2)
        binding.productRvDetail.addItemDecoration(HorizantalDoubleDivider())
        binding.productRvDetail.adapter = ItemAdapterRv(requireActivity(), allProductList)
    }


}