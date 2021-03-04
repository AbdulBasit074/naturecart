package com.example.naturescart.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCategoryDetailBinding
import com.example.naturescart.helper.HorizantalDivider
import com.example.naturescart.model.CategoryProducts

class CategoryDetailProductsRvAdapter(
    private val context: Activity,
    private val items: ArrayList<CategoryProducts>,
    private val seeAll: (Long, String) -> Unit
) :
    RecyclerView.Adapter<CategoryDetailProductsRvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.li_category_detail, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    inner class ViewHolder(val binding: LiCategoryDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: CategoryProducts) {
            binding.categoryProduct = item
            binding.productRv.adapter = ItemAdapterRv(context, item.products!!, item.name ?: "")
            binding.productRv.addItemDecoration(HorizantalDivider())
            binding.seeAll.setOnClickListener {
                seeAll(item.id?:0, item.name ?: "")
            }

        }
    }


}

