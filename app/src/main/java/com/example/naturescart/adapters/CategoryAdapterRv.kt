package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCategoryBinding
import com.example.naturescart.model.Category

class CategoryAdapterRv(private val items: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapterRv.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_category,
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

    inner class ViewHolder(val binding: LiCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: Category) {
            binding.category = item
        }
    }


}

