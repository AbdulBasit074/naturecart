package com.example.naturescart.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.naturescart.R
import com.example.naturescart.adapters.OrderRvAdapter
import com.example.naturescart.databinding.FragmentOrderBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.OrderDetail
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.order.OrderService
import com.example.naturescart.services.product.ProductService
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.example.naturescart.ui.OrderDetailActivity
import com.example.naturescart.ui.UserDetailActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class OrderFragment : Fragment(), Results {

    private val ordersDataPersistenceKey = "ordersDataPersistenceKey"
    private lateinit var orderBinding: FragmentOrderBinding
    private var orderList: ArrayList<OrderDetail> = ArrayList()
    private var loggedUser: User? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = true
    private val ordersRequest: Int = 1203
    private val loadMoreRequest: Int = 5203
    private val loginRequest: Int = 5103
    private var pageNo: Int = 1
    private lateinit var adapter: OrderRvAdapter
    private var layoutManger = LinearLayoutManager(activity)
    private lateinit var paginationListeners: PaginationListeners
    private var loadingView: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        orderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        return orderBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingView = LoadingDialog(requireContext())
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
        setAdapterForOrder()
        setListeners()
        callOrderDetail()
    }

    private fun callOrderDetail() {
        if (loggedUser != null) {
            val ordersData = Persister.with(requireContext()).getPersisted(ordersDataPersistenceKey, null)
            if (ordersData == null)
                loadingView?.show()
            else
                onSuccess(ordersRequest, ordersData)
            OrderService(ordersRequest, this).getOrders(loggedUser!!.accessToken, PaginationListeners.pageSize, pageNo)
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

        orderBinding.toolBar.appLogo.setOnClickListener {
            EventBus.getDefault().postSticky(LogoClickEvent())
        }
        orderBinding.toolBar.profileBtn.setOnClickListener {
            if (loggedUser == null)
                moveForResultFragment(requireActivity(), MenuActivity::class.java, loginRequest)
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

                loadingView?.dismiss()
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
                Persister.with(requireContext()).persist(ordersDataPersistenceKey, data)
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
        loadingView?.dismiss()
        showToast(data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                loginRequest -> {
                    loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
                    callOrderDetail()
                }
            }
        }
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
    fun onLogoutEvent(event: LogoutEvent) {
        orderList.clear()
        orderBinding.orderRv.adapter?.notifyDataSetChanged()
        orderBinding.noOrdersContainer.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserLoggedIn(event: LogInEvent) {
        loggedUser = NatureDb.getInstance(requireContext()).userDao().getLoggedUser()
        callOrderDetail()
    }
}