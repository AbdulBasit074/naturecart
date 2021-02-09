package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCartOrderBinding

class OrderItemRvAdapter(private val imageSliderList: ArrayList<String>) :
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
        return imageSliderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindView(imageSliderList[position])
    }

    inner class ViewHolder(val binding: LiCartOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(s: String) {

        }
    }


}

