package com.example.naturescart.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.adapters.OrderItemRvAdapter
import com.example.naturescart.databinding.ActivityOrderDetailBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.HorizantalMidDivider
import com.example.naturescart.helper.convertDate
import com.example.naturescart.helper.setLanguage
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.OrderDetail

class OrderDetailActivity : AppCompatActivity() {


    companion object {
        fun newInstance(context: Context, orderDetail: OrderDetail): Intent {
            return Intent(context, OrderDetailActivity::class.java).putExtra(
                Constants.orderDetail,
                orderDetail
            )
        }
    }

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var orderDetail: OrderDetail
    private var list: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        orderDetail =
            this.intent.getParcelableExtra<OrderDetail>(Constants.orderDetail) as OrderDetail
        setAdapter()
        setOrderData()
        setListeners()
    }

    private fun setOrderData() {
        binding.orderStatus.text = StringBuilder().append(Constants.getTranslate(this, "order_status")).append(orderDetail.status)
        binding.dateTxt.text = convertDate(orderDetail.orderData.toString())
        binding.itemCharges.text = getString(R.string.aed_price, String.format("%.2f", orderDetail.summary?.subTotal))
        binding.deliveryCharges.text = getString(R.string.aed_price, String.format("%.2f", orderDetail.summary?.deliveryChanges))
        binding.vatCharges.text = getString(R.string.aed_price, String.format("%.2f", orderDetail.summary?.vat))
        if (orderDetail.summary?.couponDiscount!! > 0) {
            binding.discountTitle.visibility = View.VISIBLE
            binding.discountAmount.visibility = View.VISIBLE
            binding.discountAmount.text = getString(R.string.aed_price, String.format("%.2f", orderDetail.summary?.couponDiscount))
        }
        binding.addressTitleSelect.text = orderDetail.address!![0].addressNick.toString()
        binding.addressDetail.text = orderDetail.address!![0].address.toString()
        binding.orderNo.text = getString(R.string.order_no, orderDetail.orderId.toString())
        binding.bottomSheetTotal.total.text = getString(R.string.aed_price, String.format("%.2f", orderDetail.total))
    }

    private fun setListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setAdapter() {
        binding.itemsRv.adapter = OrderItemRvAdapter(orderDetail.items as ArrayList<CartDetail.Item>)
        binding.itemsRv.addItemDecoration(
            HorizantalMidDivider(
                ContextCompat.getDrawable(this, R.drawable.divider)
            )
        )
    }
}