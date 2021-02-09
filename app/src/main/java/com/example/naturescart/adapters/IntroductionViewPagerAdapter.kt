package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiViewPagerBinding

class IntroductionViewPagerAdapter() :
    RecyclerView.Adapter<IntroductionViewPagerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_view_pager,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews()
    }

    inner class ViewHolder(private val binding: LiViewPagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindViews() {
            when (adapterPosition) {
                0 -> {
                    Glide.with(binding.root.context).load(R.drawable.introduction_first)
                        .into(binding.iconIntroduction)
                    binding.titleIntroduction.text =
                        binding.root.context.getString(R.string.intro_first_title)
                    binding.detailIntroduction.text =
                        binding.root.context.getString(R.string.intro_first_detail)
                }
                1 -> {
                    Glide.with(binding.root.context).load(R.drawable.introduction_second)
                        .into(binding.iconIntroduction)
                    binding.titleIntroduction.text =
                        binding.root.context.getString(R.string.intro_second_title)
                    binding.detailIntroduction.text =
                        binding.root.context.getString(R.string.intro_second_detail)

                }
                2 -> {
                    Glide.with(binding.root.context).load(R.drawable.introduction_third)
                        .into(binding.iconIntroduction)
                    binding.titleIntroduction.text =
                        binding.root.context.getString(R.string.intro_third_title)
                    binding.detailIntroduction.text =
                        binding.root.context.getString(R.string.intro_third_detail)
                }

            }
        }

    }

}