package com.example.naturescart.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiItemBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.Product
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.cart.CartService
import com.example.naturescart.services.product.ProductService
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class ItemAdapterRv(
    private val context: Activity,
    private val items: ArrayList<Product>,
    private val parentCategoryName: String
) : RecyclerView.Adapter<ItemAdapterRv.ViewHolder>() {

    private var isLoaderVisible = false
    private var itemView: Int = 1
    private var totalItemSelect: Int = 1
    private val loadingView = 0
    private val productFavouriteRequest: Int = 8222
    private var cartID: Long? = null
    private val addToCartRequest = 222
    private val loggedUser: User? = NatureDb.getInstance(context).userDao().getLoggedUser()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            itemView -> {
                ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.li_item, parent, false))
            }
            else -> {
                ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.li_loading_bottom, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLoaderVisible) items.size + 1 else items.size
    }

    fun startLoading() {
        isLoaderVisible = true
        notifyDataSetChanged()
    }

    fun stopLoading() {
        isLoaderVisible = false
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible && position == items.size) loadingView else itemView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == itemView)
            holder.bindView(items[position])

    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: Product) {
            if (binding is LiItemBinding) {

                binding.product = item

                binding.labelSold.visibility = if (item.quantity == 0) View.VISIBLE else View.GONE
                binding.addToCartBtn.visibility = if (item.quantity == 0) View.GONE else View.VISIBLE
                if (NatureDb.getInstance(context).favouriteDao().getProduct(item.id!!) != null) {
                    binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav_add))
                }

                binding.favouriteImage.setOnClickListener {

                    if (loggedUser != null) {
                        ProductService(productFavouriteRequest, object : Results {
                            override fun onSuccess(requestCode: Int, data: String) {
                                if (NatureDb.getInstance(context).favouriteDao().getProduct(item.id!!) == null) {
                                    item.categoryName = parentCategoryName
                                    NatureDb.getInstance(context).favouriteDao().insertProduct(item)
                                    binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav_add))
                                } else {
                                    NatureDb.getInstance(context).favouriteDao().removeProduct(item.id!!)
                                    binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav))
                                }
                                EventBus.getDefault().postSticky(FavoritesUpdatedEvent())
                                val dialog = DialogCustom(context, R.drawable.ic_add_fav, data)
                                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                                dialog.showDialog()
                            }

                            override fun onFailure(requestCode: Int, data: String) {
                                val dialog = DialogCustom(context, R.drawable.ic_add_fav, data)
                                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                                dialog.showDialog()
                            }

                        }).addToFavourite(loggedUser.accessToken, item.id!!)
                    } else {
                        val dialogMsg: String
                        if (NatureDb.getInstance(context).favouriteDao().getProduct(item.id!!) == null) {
                            item.categoryName = parentCategoryName
                            NatureDb.getInstance(context).favouriteDao().insertProduct(item)
                            binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav_add))
                            dialogMsg = "Add to favourite"
                        } else {
                            NatureDb.getInstance(context).favouriteDao().removeProduct(item.id!!)
                            binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav))
                            dialogMsg = "Remove from favourite"
                        }
                        EventBus.getDefault().postSticky(FavoritesUpdatedEvent())
                        val dialog = DialogCustom(context, R.drawable.ic_add_fav, dialogMsg)
                        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                        dialog.showDialog()
                    }
                }
                binding.itemCount.setOnClickListener {
                    setSelectCount()
                }
                binding.addToCartBtn.setOnClickListener {
                    cartID = PreferenceManager.getDefaultSharedPreferences(binding.addToCartBtn.context).getLong(Constants.cartID, 0)
                    CartService(addToCartRequest, object : Results {
                        override fun onSuccess(requestCode: Int, data: String) {
                            val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                            val dialog = DialogCustom(context, R.drawable.ic_cart, "Added To Cart")
                            dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                            dialog.showDialog()
                            EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                        }

                        override fun onFailure(requestCode: Int, data: String) {
                            val dialog = DialogCustom(context, R.drawable.ic_cart, data)
                            dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                            dialog.showDialog()
                        }

                    }).addToCart(item.id!!, totalItemSelect, cartID)
                }
            }
        }

        private fun setSelectCount() {
            if (binding is LiItemBinding) {
                /**number of product item select dialog **/
                val list: ArrayList<Int> = arrayListOf(1, 2, 3, 5, 7)
                val builder = AlertDialog.Builder(binding.root.context)
                val adapter = ArrayAdapter(binding.root.context, R.layout.support_simple_spinner_dropdown_item, list)
                builder.setAdapter(adapter) { _, which ->
                    totalItemSelect = list[which]
                    binding.itemCount.text = list[which].toString()
                }
                val dialog = builder.create()
                dialog.setCustomTitle(binding.root.context.customTextView("Select Item"))
                dialog.show()
            }
        }

    }
}

