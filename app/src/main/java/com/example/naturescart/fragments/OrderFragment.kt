package com.example.naturescart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.naturescart.R
import com.example.naturescart.adapters.OrderRvAdapter
import com.example.naturescart.databinding.FragmentOrderBinding
import com.example.naturescart.helper.PaginationListeners
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.OrderDetail
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.order.OrderService
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.example.naturescart.ui.OrderDetailActivity
import com.example.naturescart.ui.UserDetailActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class OrderFragment : Fragment(), Results {

    private lateinit var orderBinding: FragmentOrderBinding
    private var orderList: ArrayList<OrderDetail> = ArrayList()
    private var loggedUser: User? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = true
    private val ordersRequest: Int = 1203
    private val loadMoreRequest: Int = 5203
    private var pageNo: Int = 1
    private lateinit var adapter: OrderRvAdapter
    private var layoutManger = LinearLayoutManager(activity)
    private lateinit var paginationListeners: PaginationListeners

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        orderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        return orderBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
        setAdapterForOrder()
        setListeners()

        if (loggedUser != null) {
            OrderService(ordersRequest, this).getOrders(
                loggedUser!!.accessToken,
                PaginationListeners.pageSize,
                pageNo
            )
        } else {
            orderBinding.noOrdersContainer.visibility = View.VISIBLE
        }
    }

    private fun setAdapterForOrder() {
        adapter = OrderRvAdapter(orderList) { order -> onOrderClick(order) }
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        orderBinding.orderRv.layoutManager = layoutManger
        orderBinding.orderRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
    }

    private fun onOrderClick(order: OrderDetail) {
        moveFromFragment(OrderDetailActivity.newInstance(requireActivity(), order))
    }


    private fun setListeners() {
        orderBinding.toolBar.profileBtn.setOnClickListener {
            if (loggedUser == null)
                moveFromFragment(requireActivity(), MenuActivity::class.java)
            else
                moveFromFragment(requireActivity(), UserDetailActivity::class.java)
        }
        orderBinding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(requireActivity(), NotificationActivity::class.java)
        }

    }

    private fun initPageListener() {
        paginationListeners = object : PaginationListeners(layoutManger) {
            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun loadMoreItems() {
                isLoading = true
                pageNo++
                OrderService(loadMoreRequest, this@OrderFragment).getOrders(
                    loggedUser!!.accessToken,
                    pageSize,
                    pageNo
                )
                adapter.startLoading()
            }
        }


    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            ordersRequest -> {
                orderList.clear()
                isLoading = false
                orderList.addAll(Gson().fromJson(data, object : TypeToken<ArrayList<OrderDetail>>() {}.type))
                adapter.stopLoading()

                if (orderList.size < PaginationListeners.pageSize)
                    isLastPage = true
                initPageListener()
                orderBinding.orderRv.addOnScrollListener(paginationListeners)
                orderBinding.orderRv.adapter?.notifyDataSetChanged()
                orderBinding.noOrdersContainer.visibility = if (orderList.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
            loadMoreRequest -> {
                adapter.stopLoading()
                isLoading = false
                val listNewOrder = Gson().fromJson(
                    data,
                    object : TypeToken<ArrayList<OrderDetail>>() {}.type
                ) as ArrayList<OrderDetail>
                orderList.addAll(listNewOrder)
                if (listNewOrder.size < PaginationListeners.pageSize)
                    isLastPage = true
                orderBinding.orderRv.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}