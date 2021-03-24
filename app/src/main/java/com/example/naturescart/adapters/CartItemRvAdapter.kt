package com.example.naturescart.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.LiCartItemBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CartDetail
import com.example.naturescart.services.Results
import com.example.naturescart.services.cart.CartService
import com.example.naturescart.ui.ImageViewActivity
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class CartItemRvAdapter(
    private var itemList: ArrayList<CartDetail.Item>,
    private val context: Activity,
    private var refreshCallBack: () -> Unit
) :
    RecyclerView.Adapter<CartItemRvAdapter.ViewHolder>() {
    private var totalItemSelect: Int = 0
    private var cartID: Long? = null
    private val incrementRc = 3821
    private val decrementRc = 8343
    private val removeFromCartRc = 8323

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
            binding.nameTv.text = item.product?.name.toString()
            binding.itemCountTv.text = item.quantity.toString()
            totalItemSelect = item.quantity!!
            binding.priceTv.text = context.getString(R.string.aed_price, item.price.toString())
            binding.totalPriceTv.text = context.getString(R.string.aed_price, String.format("%.2f", item.price!! * item.quantity!!))
            Glide.with(context).load(item.product?.image).into(binding.iconIv)
            binding.iconIv.setOnClickListener {
                val pair: Pair<View, String> = Pair.create(binding.iconIv as View?, "ProductIcon")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, pair)
                context.startActivity(ImageViewActivity.newInstance(context, item.product?.image ?: ""), options.toBundle())
            }
            binding.incrementBtn.setOnClickListener {
                val count = binding.itemCountTv.text.toString().toInt()
                updateCart(binding.incrementBtn.context, item.product?.id!!, count + 1, incrementRc)
            }
            binding.decrementBtn.setOnClickListener {
                val count = binding.itemCountTv.text.toString().toInt()
                if (count > 1) {
                    updateCart(binding.incrementBtn.context, item.product?.id!!, count - 1, decrementRc)
                } else {
                    deleteFromCart(item.id)
                }
            }
            binding.deleteBtn.setOnClickListener {
                deleteFromCart(item.id)
                binding.parentView.close(true)
            }
        }

        private fun updateCart(context: Context, itemId: Long, quantity: Int, requestCode: Int) {
            val loadingDialog = LoadingDialog(context)
            loadingDialog.show()
            cartID = PreferenceManager.getDefaultSharedPreferences(context).getLong(Constants.cartID, 0)
            CartService(requestCode, object : Results {
                override fun onSuccess(requestCode: Int, data: String) {
                    Handler(Looper.getMainLooper()).postDelayed({

                    val count = binding.itemCountTv.text.toString().toInt()
                    if (requestCode == incrementRc)
                        binding.itemCountTv.text = (count + 1).toString()
                    else
                        binding.itemCountTv.text = (count - 1).toString()



                    loadingDialog.dismiss()
                    val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                    EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))

                    }, 1000)

                }

                override fun onFailure(requestCode: Int, data: String) {
                    loadingDialog.dismiss()
                    val dialog = DialogCustom(context, R.drawable.ic_cart, data)
                    dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                    dialog.showDialog()
                }
            }).addToCart(itemId, quantity, cartID)
        }

        private fun deleteFromCart(itemId: Long?) {
            val loadingDialog = LoadingDialog(context)
            loadingDialog.show()
            CartService(removeFromCartRc, object : Results {
                override fun onSuccess(requestCode: Int, data: String) {
                    loadingDialog.dismiss()
                    refreshCallBack()
                    val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                    EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                }

                override fun onFailure(requestCode: Int, data: String) {
                    loadingDialog.dismiss()
                    context.showToast(data)
                }

            }).removeFromCart(itemId ?: 0)
        }
    }
}