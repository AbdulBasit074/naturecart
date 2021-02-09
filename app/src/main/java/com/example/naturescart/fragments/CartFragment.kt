package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.naturescart.R
import com.example.naturescart.adapters.CartItemRvAdapter
import com.example.naturescart.databinding.FragmentCartBinding
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.ui.CartOrderDetailActivity

class CartFragment : Fragment() {

    private lateinit var cartBinding: FragmentCartBinding
    private var list: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        cartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        return cartBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setAdapter()
        setListeners()

    }

    private fun setListeners() {
        cartBinding.nextBtn.setOnClickListener {
            moveFromFragment(activity!!, CartOrderDetailActivity::class.java)
        }
    }

    private fun setAdapter() {
        cartBinding.cartRv.adapter = CartItemRvAdapter(list)
    }

    private fun setData() {
        list.add("dummy Data")
        list.add("dummy Data")
        list.add("dummy Data")
        list.add("dummy Data")
        list.add("dummy Data")
        list.add("dummy Data")

    }


}