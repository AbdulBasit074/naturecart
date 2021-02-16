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

class CategoryListingFragment : Fragment() {
    private lateinit var binding: FragmentCategoryListingBinding
    private var listItems: ArrayList<String> = ArrayList()
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
        dataSet()
        binding.categoryRvDetail.layoutManager = GridLayoutManager(activity, 2)
//        binding.categoryRvDetail.adapter = ItemAdapterRv(listItems)
        binding.categoryRvDetail.addItemDecoration(HorizantalDoubleDivider())
    }

    private fun dataSet() {

        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
        listItems.add("Dummy Text")
    }

}