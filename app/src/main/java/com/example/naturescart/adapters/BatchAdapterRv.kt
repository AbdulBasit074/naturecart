package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiBatchBinding
import com.example.naturescart.databinding.LiSliderBinding

class BatchAdapterRv(private val imageSliderList: ArrayList<String>) :
    RecyclerView.Adapter<BatchAdapterRv.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_batch,
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

    inner class ViewHolder(val binding: LiBatchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(s: String) {
            Glide.with(binding.imageBatch.context).load(R.drawable.dummy).into(binding.imageBatch)
        }
    }


}

