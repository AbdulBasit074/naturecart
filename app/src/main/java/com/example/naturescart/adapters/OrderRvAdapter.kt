package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiOrderBinding
import com.example.naturescart.model.OrderDetail

class OrderRvAdapter(
    private val items: ArrayList<OrderDetail>,
    private val onOrderClick: (OrderDetail) -> Unit

) :
    RecyclerView.Adapter<OrderRvAdapter.ViewHolder>() {

    private var isLoaderVisible = false
    private var itemView: Int = 1
    private val loadingView = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {
            itemView -> {
                ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.li_order,
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.li_loading_bottom,
                        parent,
                        false
                    )
                )
            }
        }


    }

    override fun getItemCount(): Int {
        return if (isLoaderVisible) items.size + 1 else items.size
    }

    fun startLoading() {
        isLoaderVisible = true
        notifyDataSetChanged()
    }

    fun stopLoading() {
        isLoaderVisible = false
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible && position == items.size) loadingView else itemView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == itemView)
            holder.bindView(items[position])

    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(order: OrderDetail) {
            if (binding is LiOrderBinding) {
                binding.model = order
                setAddressOrder(binding.deliverDetail, order.address!![0])
                binding.containerOrder.setOnClickListener {
                    onOrderClick(order)
                }
            }

        }


    }


}

