package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiSearchBinding
import com.example.naturescart.model.SearchHistory

class SearchAdapterRv(
    private val items: ArrayList<SearchHistory>,
    private val onClickHistory: (String) -> Unit
) :
    RecyclerView.Adapter<SearchAdapterRv.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_search,
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

    inner class ViewHolder(val binding: LiSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: SearchHistory) {
            binding.searchText.text = item.keyword.toString()
            binding.parentView.setOnClickListener {
                onClickHistory(item.keyword.toString()!!)
            }
        }
    }


}

