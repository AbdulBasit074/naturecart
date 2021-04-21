package com.example.naturescart.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private var totalItemSelect: Float = 0f
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
            var factorIncrement: Float = 0.5f
            factorIncrement = if (item.product!!.factor!! > 0.5f)
                1f
            else
                0.5f

            binding.nameTv.text = item.product?.name.toString()
            showItemCountText(binding.itemCountTv, item.quantity!!, factorIncrement)
            totalItemSelect = item.quantity!!
            binding.priceTv.text = context.getString(R.string.aed_price, item.product!!.sellPrice.toString())
            binding.descriptionTv.text = item.product!!.description.toString()
            binding.discountOfferTv.text = context.getString(R.string.aed_price, item.product!!.discounted_price.toString())
            binding.totalPriceTv.text = context.getString(R.string.aed_price, String.format("%.2f", item.amount))
            Glide.with(context).load(item.product?.image).into(binding.iconIv)
            binding.discountOfferTv.visibility =
                if (item.product!!.is_offer_valid!!) {
                    setTextSlashCartPrice(binding.priceTv, item.product!!.sellPrice!!)
                    setTextSlashCartPriceAdapter(binding.discountOfferTv, item.product!!.discounted_price!!)
                    View.VISIBLE
                } else {
                    setTextSlashCartPriceAdapter(binding.priceTv, item.product!!.sellPrice!!)
                    View.GONE
                }


            binding.iconIv.setOnClickListener {
                val pair: Pair<View, String> = Pair.create(binding.iconIv as View?, "ProductIcon")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, pair)
                context.startActivity(ImageViewActivity.newInstance(context, item.product?.image ?: ""), options.toBundle())
            }
            binding.incrementBtn.setOnClickListener {
                val count = binding.itemCountTv.text.toString().toFloat()
                updateCart(binding.incrementBtn.context, item.product?.id!!, count + factorIncrement, incrementRc, factorIncrement)
            }
            binding.decrementBtn.setOnClickListener {
                val count = binding.itemCountTv.text.toString().toFloat()
                if (count > factorIncrement) {
                    updateCart(binding.incrementBtn.context, item.product?.id!!, count - factorIncrement, decrementRc, factorIncrement)
                } else {
                    deleteFromCart(item.id)
                }
            }
            binding.deleteBtn.setOnClickListener {
                deleteFromCart(item.id)
                binding.parentView.close(true)
            }
        }

        private fun updateCart(context: Context, itemId: Long, quantity: Float, requestCode: Int, factorIncrement: Float) {

            cartID = PreferenceManager.getDefaultSharedPreferences(context).getLong(Constants.cartID, 0)
            CartService(requestCode, object : Results {
                override fun onSuccess(requestCode: Int, data: String) {

                    val count = binding.itemCountTv.text.toString().toFloat()
                    if (requestCode == incrementRc)
                        showItemCountText(binding.itemCountTv, (count + factorIncrement), factorIncrement)
                    else
                        showItemCountText(binding.itemCountTv, (count - factorIncrement), factorIncrement)

                    val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                    EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))


                }

                override fun onFailure(requestCode: Int, data: String) {
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

        fun showItemCountText(textView: TextView, value: Float, factor: Float) {
            var afterDigit = 1
            if (factor > 0.5)
                afterDigit = 0
            if (value == 0f)
                afterDigit = 0

            textView.text = (String.format("%.$afterDigit" + "f", value))
        }
    }
}