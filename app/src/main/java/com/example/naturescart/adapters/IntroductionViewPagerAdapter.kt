package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiViewPagerBinding
import com.example.naturescart.model.OnBoarding

class IntroductionViewPagerAdapter(private val onBoard: ArrayList<OnBoarding>) :
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
        return onBoard.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(onBoard[position])
    }

    inner class ViewHolder(private val binding: LiViewPagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindViews(board: OnBoarding) {
                   Glide.with(binding.root.context).load(board.image)
                        .into(binding.iconIntroduction)
                    binding.titleIntroduction.text = board.name
                    binding.detailIntroduction.text = board.description
        }

    }

}