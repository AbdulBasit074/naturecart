package com.example.naturescart.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class ItemAdapterFNRv(
    private val context: Activity,
    private val items: ArrayList<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ItemAdapterFNRv.ViewHolder>() {

    private var isLoaderVisible = false
    private var itemView: Int = 1
    private val loadingView = 0
    private val productFavouriteRequest: Int = 8222
    private val removeFromCartRc = 222
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

                var factorIncrement: Float = 0.5f

                binding.itemContainer.setOnClickListener {
                    onItemClick(item)
                }
                binding.product = item

                factorIncrement = if (item.factor!! > 0.5f)
                    1f
                else
                    0.5f

                showItemCountText(binding.itemCountTv, Persister.with(context).getCartQuantity(item.id), factorIncrement)
                binding.descriptionTv.visibility = if (item.description.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.discountOfferTv.visibility =
                    if (item.is_offer_valid!!) {
                        setTextSlashPrice(binding.priceTv, binding.priceTv.text.toString())
                        setTextPriceSlashAdapter(binding.discountOfferTv, item.discounted_price!!.toFloat())
                        binding.saveLabel.visibility = View.VISIBLE
                        View.VISIBLE
                    } else {
                        setTextPriceSlashAdapter(binding.priceTv, item.sellPrice!!.toFloat())
                        binding.saveLabel.visibility = View.GONE
                        View.GONE
                    }
                if (NatureDb.getInstance(context).favouriteDao().getProduct(item.id!!) != null) {
                    binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav_add))
                } else {
                    binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav))
                }
                binding.favouriteImage.setOnClickListener {
                    val loadingDialog = LoadingDialog(context)
                    if (loggedUser != null) {
                        loadingDialog.show()
                        ProductService(productFavouriteRequest, object : Results {
                            override fun onSuccess(requestCode: Int, data: String) {
                                loadingDialog.dismiss()
                                if (NatureDb.getInstance(context).favouriteDao().getProduct(item.id!!) == null) {
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
                                loadingDialog.dismiss()
                                val dialog = DialogCustom(context, R.drawable.ic_add_fav, data)
                                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                                dialog.showDialog()
                            }
                        }).addToFavourite(loggedUser.accessToken, item.id!!)
                    } else {
                        loadingDialog.dismiss()
                        val dialogMsg: String
                        if (NatureDb.getInstance(context).favouriteDao().getProduct(item.id!!) == null) {
                            NatureDb.getInstance(context).favouriteDao().insertProduct(item)
                            binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav_add))
                            dialogMsg = Constants.getTranslate(context, "add_to_fav")
                        } else {
                            NatureDb.getInstance(context).favouriteDao().removeProduct(item.id!!)
                            binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav))
                            dialogMsg = Constants.getTranslate(context, "remove_from_fav")
                        }
                        EventBus.getDefault().postSticky(FavoritesUpdatedEvent())
                        val dialog = DialogCustom(context, R.drawable.ic_add_fav, dialogMsg)
                        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                        dialog.showDialog()
                    }
                }
                binding.incrementBtn.setOnClickListener {
                    val count = binding.itemCountTv.text.toString().toFloat()
                    addToCart(binding.incrementBtn.context, item.id!!, count + factorIncrement, binding.itemCountTv, factorIncrement)
                }
                binding.decrementBtn.setOnClickListener {
                    val count = binding.itemCountTv.text.toString().toFloat()
                    if (count > factorIncrement) {
                        addToCart(binding.incrementBtn.context, item.id!!, count - factorIncrement, binding.itemCountTv, factorIncrement)
                    } else if (count == factorIncrement) {
                        showItemCountText(binding.itemCountTv, (count - factorIncrement), factorIncrement)
                        deleteFromCart(Persister.with(context).getCartItemId(item.id))
                    }
                }
            }

        }

        private fun addToCart(context: Context, itemId: Long, quantity: Float, textView: TextView, factorIncrement: Float) {
            cartID = PreferenceManager.getDefaultSharedPreferences(context).getLong(Constants.cartID, 0)
            CartService(addToCartRequest, object : Results {
                override fun onSuccess(requestCode: Int, data: String) {
                    showItemCountText(textView, quantity, factorIncrement)
                    val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                    EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                    EventBus.getDefault().postSticky(CartItemAddedEvent(cartDetail.items?.size ?: 0, cartDetail.subTotal))
                }

                override fun onFailure(requestCode: Int, data: String) {
                    context.showToast(data)
                }
            }).addToCart(itemId, quantity, cartID)
        }

        private fun deleteFromCart(itemId: Long?) {
            CartService(removeFromCartRc, object : Results {
                override fun onSuccess(requestCode: Int, data: String) {
                    val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                    EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                }

                override fun onFailure(requestCode: Int, data: String) {
                    context.showToast(data)
                }
            }).removeFromCart(itemId ?: 0)
        }
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