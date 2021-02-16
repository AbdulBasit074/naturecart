package com.example.naturescart.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import com.example.naturescart.databinding.LiItemBinding
import com.example.naturescart.helper.DialogCustom
import com.example.naturescart.helper.customTextView
import com.example.naturescart.model.Product
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.product.ProductService

class ItemAdapterRv(
    private val context: Activity,
    private val items: ArrayList<Product>,
    private val forLogin: () -> Unit
) :
    RecyclerView.Adapter<ItemAdapterRv.ViewHolder>(), Results {

    private var isLoaderVisible = false
    private var itemView: Int = 1
    private val loadingView = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            itemView -> {
                ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.li_item,
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.li_loading_bottom,
                        parent,
                        false
                    )
                )
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
        holder.bindView(items[position])
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        private val productFavourite: Int = 8222
        var loggedUser: User? = null

        fun bindView(item: Product) {
            if (binding is LiItemBinding) {
                loggedUser = NatureDb.newInstance(context).userDao().getLoggedUser()
                binding.product = item

                binding.itemCount.setOnClickListener {
                    setSelectCount()
                }
                binding.favouriteImage.setOnClickListener {
                    if (loggedUser == null)
                        forLogin()
                    else
                        ProductService(
                            productFavourite,
                            this@ItemAdapterRv
                        ).addToFavourite(loggedUser!!.accessToken)
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

        }

        private fun setSelectCount() {
            if (binding is LiItemBinding) {
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

    override fun onSuccess(requestCode: Int, data: String) {
        val dialog = DialogCustom(
            context,
            R.drawable.ic_add_fav,
            data
        )
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.show()

    }

    override fun onFailure(requestCode: Int, data: String) {
        val dialog = DialogCustom(
            context,
            R.drawable.ic_add_fav,
            data
        )
        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        dialog.show()

    }


}

