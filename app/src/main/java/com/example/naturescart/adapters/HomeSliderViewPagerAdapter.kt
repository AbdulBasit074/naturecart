package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiSliderBinding
import com.example.naturescart.model.Banner

class HomeSliderViewPagerAdapter(private val imageSliderList: ArrayList<Banner>) :
    RecyclerView.Adapter<HomeSliderViewPagerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_slider,
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

    inner class ViewHolder(val binding: LiSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(s: Banner) {
            setImage(binding.sliderImage, s.image)
        }
    }


}

