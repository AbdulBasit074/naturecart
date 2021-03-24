package com.example.naturescart.adapters

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

    private var positionSelect = Constants.selectAddressId
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

        fun bindView(item: Address) {
            binding.addressTitle.text = item.addressNick
            binding.addressDetail.text = Constants.geoCoding(
                item.latitude!!,
                item.longitude!!,
                binding.root.context
            )
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
                    Constants.selectAddressId = adapterPosition
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
            binding.deleteBtn.context.showToast("Address Deleted")
        }

        override fun onFailure(requestCode: Int, data: String) {
            binding.deleteBtn.context.showToast(data)
        }

    }


}

