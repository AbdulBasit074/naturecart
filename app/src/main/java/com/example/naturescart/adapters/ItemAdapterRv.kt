package com.example.naturescart.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiItemBinding
import com.example.naturescart.helper.DialogCustom
import com.example.naturescart.helper.customTextView

class ItemAdapterRv(private val imageSliderList: ArrayList<String>) :
    RecyclerView.Adapter<ItemAdapterRv.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_item,
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

    inner class ViewHolder(val binding: LiItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(s: String) {
            binding.itemCount.setOnClickListener {
                setSelectCount()
            }


            binding.favouriteImage.setOnClickListener {
                val dialog = DialogCustom(
                    binding.root.context,
                    R.drawable.ic_add_fav,
                    "Added To Favourite"
                )
                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                dialog.show()
            }
            binding.itemCart.setOnClickListener {
                val dialog = DialogCustom(
                    binding.root.context,
                    R.drawable.ic_cart,
                    "Added To Cart"
                )
                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                dialog.show()

            }

        }

        private fun setSelectCount() {
            /**number of product item select dialog **/
            val list: ArrayList<Int> = arrayListOf(1, 2, 3, 5, 7)
            val builder = AlertDialog.Builder(binding.root.context)
            val adapter = ArrayAdapter(
                binding.root.context,
                R.layout.support_simple_spinner_dropdown_item,
                list
            )
            builder.setAdapter(adapter) { _, which ->
                binding.itemCount.text = list[which].toString()
            }
            val dialog = builder.create()
            dialog.setCustomTitle(binding.root.context.customTextView("Select Item"))
            dialog.show()
        }

    }


}

