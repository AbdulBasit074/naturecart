package com.example.naturescart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiAddressBinding

class AddressessRvAdapter(
    private val detailList: ArrayList<String>,
    private val selection: Boolean
) :
    RecyclerView.Adapter<AddressessRvAdapter.ViewHolder>() {
    private var positionSelect = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_address,
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

    inner class ViewHolder(val binding: LiAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(s: String) {

            if (selection) {
                Glide.with(binding.icon.context).load(R.drawable.ic_icon_check_circle)
                    .into(binding.icon)
                binding.icon.visibility =
                    if (positionSelect == adapterPosition) View.VISIBLE else View.GONE

            } else {
                Glide.with(binding.icon.context).load(R.drawable.ic_next_black).into(binding.icon)
                binding.icon.visibility = View.VISIBLE

            }
            binding.parentView.setOnClickListener {
                if (selection) {
                    positionSelect = adapterPosition
                    notifyDataSetChanged()
                }

            }
        }

    }


}

