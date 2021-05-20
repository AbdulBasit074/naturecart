package com.example.naturescart.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiAddressBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.Address
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.address.AddressService

class AddressesRvAdapter(
    private val items: ArrayList<Address>,
    private val selection: Boolean,
    private var addressClick: (Address) -> Unit
) : RecyclerView.Adapter<AddressesRvAdapter.ViewHolder>() {

    private var user: User? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        user = NatureDb.getInstance(parent.context).userDao().getLoggedUser()
        return ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.li_address, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(items[position])

    }

    inner class ViewHolder(val binding: LiAddressBinding) : RecyclerView.ViewHolder(binding.root),
        Results {

        private val addressDelete: Int = 222
        private var deletedSelect: Int = -1

        @SuppressLint("SetTextI18n")
        fun bindView(item: Address) {
            binding.addressTitle.text = item.addressNick
            binding.addressDetail.text = item.area + ", " + item.street + ", " + item.apartment
            if (selection) {
                binding.revealLayout.setLockDrag(true)
                Glide.with(binding.icon.context).load(R.drawable.ic_icon_check_circle)
                    .into(binding.icon)
                binding.icon.visibility =
                    if (Constants.selectAddressId == item.id) View.VISIBLE else View.GONE

            } else {
                Glide.with(binding.icon.context).load(R.drawable.ic_edit).into(binding.icon)
                binding.icon.visibility = View.VISIBLE
            }
            binding.parentView.setOnClickListener {
                if (selection) {
                    Constants.selectAddressId = item.id!!
                    notifyDataSetChanged()
                    addressClick(item)
                } else {
                    addressClick(item)
                }
            }
            binding.deleteBtn.setOnClickListener {
                deletedSelect = adapterPosition
                AddressService(addressDelete, this).deleteAddress(user?.accessToken ?: "", item.id!!)
            }
        }

        override fun onSuccess(requestCode: Int, data: String) {
            binding.revealLayout.close(true)
            items.removeAt(deletedSelect)
            notifyDataSetChanged()
            binding.deleteBtn.context.showToast(Constants.getTranslate(binding.icon.context, "address_deleted"))
        }

        override fun onFailure(requestCode: Int, data: String) {
            binding.deleteBtn.context.showToast(data)
        }


    }


}
    


