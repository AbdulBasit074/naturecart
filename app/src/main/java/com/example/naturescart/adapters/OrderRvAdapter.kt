package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiOrderBinding

class OrderRvAdapter(
    private val detailList: ArrayList<String>,
    private val onOrderClick: (String) -> Unit

) :
    RecyclerView.Adapter<OrderRvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindView(detailList[position])
    }

    inner class ViewHolder(val binding: LiOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(s: String) {
            binding.containerOrder.setOnClickListener {
                onOrderClick(s)
            }

        }


    }


}

