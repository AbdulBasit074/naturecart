package com.example.naturescart.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
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
import androidx.core.util.Pair
import com.example.naturescart.services.cart.CartService
import com.example.naturescart.services.product.ProductService
import com.example.naturescart.ui.ImageViewActivity
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class ItemAdapterRv(
    private val context: Activity,
    private val items: ArrayList<Product>,
    private val parentCategoryName: String,
    private val onItemClick: (Product) -> Unit

) : RecyclerView.Adapter<ItemAdapterRv.ViewHolder>() {

    private var isLoaderVisible = false
    private var itemView: Int = 1
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
                binding.itemContainer.setOnClickListener {
                    onItemClick(item)
                }
                binding.product = item
                binding.itemCountTv.text = Persister.with(context).getCartQuantity(item.id).toString()
                binding.descriptionTv.visibility = if (item.description.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.labelSold.visibility = if (item.quantity == 0) View.VISIBLE else View.GONE
                binding.incrementBtn.visibility = if (item.quantity == 0) View.GONE else View.VISIBLE
                binding.decrementBtn.visibility = if (item.quantity == 0) View.GONE else View.VISIBLE
                binding.itemCountTv.visibility = if (item.quantity == 0) View.GONE else View.VISIBLE
                if (NatureDb.getInstance(context).favouriteDao().getProduct(item.id!!) != null) {
                    binding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_fav_add))
                }
                binding.iconIv.setOnClickListener {
                    val pair: Pair<View, String> = Pair.create(binding.iconIv as View?, "ProductIcon")
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, pair)
                    context.startActivity(ImageViewActivity.newInstance(context, item.image ?: ""), options.toBundle())
                }
                binding.favouriteImage.setOnClickListener {
                    val loadingDialog = LoadingDialog(context)
                    if (loggedUser != null) {
                        loadingDialog.show()
                        ProductService(productFavouriteRequest, object : Results {
                            override fun onSuccess(requestCode: Int, data: String) {
                                loadingDialog.dismiss()
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
                binding.incrementBtn.setOnClickListener {
                    val count = binding.itemCountTv.text.toString().toInt()
                    if (count == item.quantity) {
                        AlertDialog.Builder(context).setTitle("Couldn't add more").setMessage("Out of stock").setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                    } else {
                        binding.itemCountTv.text = (count + 1).toString()
                        addToCart(binding.incrementBtn.context, item.id!!, count + 1)
                    }
                }
                binding.decrementBtn.setOnClickListener {
                    val count = binding.itemCountTv.text.toString().toInt()
                    if (count > 0) {
                        binding.itemCountTv.text = (count - 1).toString()
                        addToCart(binding.incrementBtn.context, item.id!!, count - 1)
                    }
                }
            }
        }

        private fun addToCart(context: Context, itemId: Long, quantity: Int) {
            val loadingDialog = LoadingDialog(context)
            loadingDialog.show()
            cartID = PreferenceManager.getDefaultSharedPreferences(context).getLong(Constants.cartID, 0)
            CartService(addToCartRequest, object : Results {
                override fun onSuccess(requestCode: Int, data: String) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog.dismiss()
                        val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                        EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                        EventBus.getDefault().postSticky(CartItemAddedEvent(cartDetail.items?.size ?: 0, cartDetail.subTotal))
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

    }
}

