package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCartOrderBinding
import com.example.naturescart.model.CartDetail

class OrderItemRvAdapter(private val items: ArrayList<CartDetail.Item>) :
    RecyclerView.Adapter<OrderItemRvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_cart_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindView(items[position])
    }

    inner class ViewHolder(val binding: LiCartOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: CartDetail.Item) {
            val product = item.product
            Glide.with(binding.iconIv.context).load(product?.image).into(binding.iconIv)
            binding.nameTv.text = product?.name
            binding.itemPriceTv.text = StringBuilder().append(binding.itemPriceTv.context.getString(R.string.aed_price, String.format("%.2f", product?.sellPrice))).append(" x ").append(item.quantity)
            binding.itemTotalTv.text = binding.itemPriceTv.context.getString(R.string.aed_price, String.format("%.2f", item.amount))
        }
    }
}

