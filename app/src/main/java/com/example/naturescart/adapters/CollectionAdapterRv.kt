package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCollectionBinding
import com.example.naturescart.model.CollectionModel

class CollectionAdapterRv(private val items: ArrayList<CollectionModel>) :
    RecyclerView.Adapter<CollectionAdapterRv.ViewHolder>() {


    var isLoaderVisible = false
    private val viewLoading: Int = 0
    private val viewItem: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {
            viewItem -> {
                ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.li_collection, parent, false))
            }
            else -> {
                ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.li_loading, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLoaderVisible) items.size + 1 else items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == viewItem)
            holder.bindView(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible && position == items.size) {
            viewLoading
        } else {
            return viewItem
        }
    }

    fun startLoading() {
        isLoaderVisible = true
        notifyDataSetChanged()
    }

    fun stopLoading() {
        isLoaderVisible = false
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: CollectionModel) {
            if (binding is LiCollectionBinding) {
                binding.collectionItem = item
            }
        }
    }


}

