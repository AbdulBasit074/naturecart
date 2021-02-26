package com.example.naturescart.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCartItemBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.customTextView
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.CartDetail
import com.example.naturescart.services.Results
import com.example.naturescart.services.cart.CartService
import com.google.gson.Gson

class CartItemRvAdapter(
    private var itemList: ArrayList<CartDetail.Item>,
    private val context: Activity,
    private var refreshCallBack: () -> Unit
) :
    RecyclerView.Adapter<CartItemRvAdapter.ViewHolder>(), Results {
    private var totalItemSelect: Int = 0
    private var cartID: Long? = null
    private val addToCartRequest = 222

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.li_cart_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindView(itemList[position])
    }

    inner class ViewHolder(val binding: LiCartItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: CartDetail.Item) {
            binding.itemCount.setOnClickListener {
                setSelectCount(item)
            }
            binding.itemDetail.text = item.product?.name.toString()
            binding.itemCount.text = item.quantity.toString()
            totalItemSelect = item.quantity!!
            binding.itemPrice.text =
                binding.itemCount.context.getString(R.string.aed_price, item.price.toString())
            binding.totalItemPrice.text = binding.itemCount.context.getString(
                R.string.aed_price,
                (item.price!! * item.quantity!!).toString()
            )
            Glide.with(binding.productImage.context).load(item.product?.image)
                .into(binding.productImage)

        }

        private fun setSelectCount(item: CartDetail.Item) {
            /**number of product item select dialog **/
            val list: ArrayList<Int> = arrayListOf(1, 2, 3, 5, 7)
            val builder = AlertDialog.Builder(binding.root.context)
            val adapter = ArrayAdapter(
                binding.root.context,
                R.layout.support_simple_spinner_dropdown_item,
                list
            )
            builder.setAdapter(adapter) { _, which ->
                totalItemSelect = list[which]

                cartID =
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(binding.totalItemPrice.context)
                        .getLong(Constants.cartID, 0)
                CartService(addToCartRequest, this@CartItemRvAdapter)
                    .addToCart(
                        item.product?.id!!,
                        totalItemSelect,
                        cartID
                    )
            }
            val dialog = builder.create()
            dialog.setCustomTitle(binding.root.context.customTextView("Select Item"))
            dialog.show()
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            addToCartRequest -> {
                val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putLong(
                        Constants.cartID,
                        cartDetail.id!!
                    ).apply()
                refreshCallBack()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        context.showToast(data)
    }


}

