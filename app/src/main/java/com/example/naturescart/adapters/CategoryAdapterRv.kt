package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCategoryBinding

class CategoryAdapterRv(private val imageSliderList: ArrayList<String>) :
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
        return imageSliderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindView(imageSliderList[position])
    }

    inner class ViewHolder(val binding: LiCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(s: String) {
            binding.categoryTxt.text = s
        }
    }


}

