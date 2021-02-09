package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.naturescart.R
import com.example.naturescart.adapters.OrderRvAdapter
import com.example.naturescart.databinding.FragmentOrderBinding
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.example.naturescart.ui.OrderDetailActivity


class OrderFragment : Fragment() {

    private lateinit var orderBinding: FragmentOrderBinding
    private var list: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        orderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        return orderBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setData()
        setAdapters()


    }

    private fun setAdapters() {
        orderBinding.orderRv.adapter = OrderRvAdapter(list) { orderId -> onOrderClick(orderId) }
    }

    private fun onOrderClick(orderId: String) {
        moveFromFragment(activity!!, OrderDetailActivity::class.java)
    }

    private fun setData() {
        list.add("Dummy Data")
        list.add("Dummy Data")
        list.add("Dummy Data")
        list.add("Dummy Data")
    }

    private fun setListeners() {

        orderBinding.toolBar.profileBtn.setOnClickListener {
            moveFromFragment(activity!!, MenuActivity::class.java)
        }
        orderBinding.toolBar.notificationBtn.setOnClickListener{
            moveFromFragment(activity!!, NotificationActivity::class.java)
        }

    }
}