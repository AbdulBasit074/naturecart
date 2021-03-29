package com.example.naturescart.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.databinding.FragmentProductDetailsBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.Product
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.cart.CartService
import com.example.naturescart.services.product.ProductService
import com.example.naturescart.ui.ImageViewActivity
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class ProductDetailsFragment(private val product: Product) : Fragment() {

    private lateinit var mBinding: FragmentProductDetailsBinding
    private var loggedUser: User? = null
    private var cartID: Long? = null
    private val productFavouriteRequest: Int = 8222
    private val addToCartRequest = 222

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loggedUser = NatureDb.getInstance(requireContext()).userDao().getLoggedUser()
        mBinding.product = product
        setListeners()
    }

    private fun setListeners() {
        mBinding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }


        mBinding.itemCountTv.text = Persister.with(requireContext()).getCartQuantity(product.id).toString()
        mBinding.descriptionTv.visibility = if (product.description.isNullOrEmpty()) View.GONE else View.VISIBLE
        mBinding.labelSold.visibility = if (product.quantity == 0) View.VISIBLE else View.GONE
        mBinding.incrementBtn.visibility = if (product.quantity == 0) View.GONE else View.VISIBLE
        mBinding.decrementBtn.visibility = if (product.quantity == 0) View.GONE else View.VISIBLE
        mBinding.itemCountTv.visibility = if (product.quantity == 0) View.GONE else View.VISIBLE
        if (NatureDb.getInstance(requireContext()).favouriteDao().getProduct(product.id!!) != null) {
            mBinding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_fav_add))
        }
        mBinding.iconIv.setOnClickListener {
            val pair: Pair<View, String> = Pair.create(mBinding.iconIv as View?, "ProductIcon")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), pair)
            context?.startActivity(ImageViewActivity.newInstance(requireContext(), product.image ?: ""), options.toBundle())
        }
        mBinding.favouriteImage.setOnClickListener {
            val loadingDialog = LoadingDialog(requireContext())
            if (loggedUser != null) {
                loadingDialog.show()
                ProductService(productFavouriteRequest, object : Results {
                    override fun onSuccess(requestCode: Int, data: String) {
                        loadingDialog.dismiss()
                        if (NatureDb.getInstance(requireContext()).favouriteDao().getProduct(product.id!!) == null) {
                            NatureDb.getInstance(requireContext()).favouriteDao().insertProduct(product)
                            mBinding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_fav_add))
                        } else {
                            NatureDb.getInstance(requireContext()).favouriteDao().removeProduct(product.id!!)
                            mBinding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_fav))
                        }
                        EventBus.getDefault().postSticky(FavoritesUpdatedEvent())
                        val dialog = DialogCustom(requireContext(), R.drawable.ic_add_fav, data)
                        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                        dialog.showDialog()
                    }

                    override fun onFailure(requestCode: Int, data: String) {
                        loadingDialog.dismiss()
                        val dialog = DialogCustom(requireContext(), R.drawable.ic_add_fav, data)
                        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                        dialog.showDialog()
                    }
                }).addToFavourite(loggedUser!!.accessToken, product.id!!)
            } else {
                loadingDialog.dismiss()
                val dialogMsg: String
                if (NatureDb.getInstance(requireContext()).favouriteDao().getProduct(product.id!!) == null) {
                    NatureDb.getInstance(requireContext()).favouriteDao().insertProduct(product)
                    mBinding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_fav_add))
                    dialogMsg = "Add to favourite"
                } else {
                    NatureDb.getInstance(requireContext()).favouriteDao().removeProduct(product.id!!)
                    mBinding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_fav))
                    dialogMsg = "Remove from favourite"
                }
                EventBus.getDefault().postSticky(FavoritesUpdatedEvent())
                val dialog = DialogCustom(requireContext(), R.drawable.ic_add_fav, dialogMsg)
                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                dialog.showDialog()
            }
        }
        mBinding.incrementBtn.setOnClickListener {
            val count = mBinding.itemCountTv.text.toString().toInt()
            if (count == product.quantity) {
                AlertDialog.Builder(requireContext()).setTitle("Couldn't add more").setMessage("Out of stock").setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            } else {
                mBinding.itemCountTv.text = (count + 1).toString()
                addToCart(mBinding.incrementBtn.context, product.id!!, count + 1)
            }
        }
        mBinding.decrementBtn.setOnClickListener {
            val count = mBinding.itemCountTv.text.toString().toInt()
            if (count > 0) {
                mBinding.itemCountTv.text = (count - 1).toString()
                addToCart(mBinding.incrementBtn.context, product.id!!, count - 1)
            }
        }


    }

    private fun addToCart(context: Context, itemId: Long, quantity: Int) {

        cartID = PreferenceManager.getDefaultSharedPreferences(context).getLong(Constants.cartID, 0)
        CartService(addToCartRequest, object : Results {
            override fun onSuccess(requestCode: Int, data: String) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                    EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                    EventBus.getDefault().postSticky(CartItemAddedEvent(cartDetail.items?.size ?: 0, cartDetail.subTotal))
                }, 1000)
            }

            override fun onFailure(requestCode: Int, data: String) {
                val dialog = DialogCustom(context, R.drawable.ic_cart, data)
                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                dialog.showDialog()
            }
        }).addToCart(itemId, quantity, cartID)
    }
}