package com.example.naturescart.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.adapters.setTextSlashPrice
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
    private val removeFromCartRc = 222
    private val productFavouriteRequest: Int = 8222
    private val addToCartRequest = 222
    private var factorIncrement: Float = 0.5f

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
        if (product.factor!! > 0.5)
            factorIncrement = 1f

        if (Persister.with(requireContext()).getCartQuantity(product.id) > 0)
            updateTotalAmount()

        showItemCountText(mBinding.itemCountTv, Persister.with(requireContext()).getCartQuantity(product.id), factorIncrement)
        mBinding.descriptionTv.visibility = if (product.description.isNullOrEmpty()) View.GONE else View.VISIBLE
        mBinding.discountOfferTv.visibility =
            if (product.is_offer_valid == true) {
                setTextSlashPrice(mBinding.priceTv, mBinding.priceTv.text.toString())
                mBinding.saveLabel.visibility = View.VISIBLE
                View.VISIBLE
            } else {
                mBinding.saveLabel.visibility = View.GONE
                View.GONE
            }





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
                        EventBus.getDefault().postSticky(updateItemCount())
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
                val dialogMsg: String = if (NatureDb.getInstance(requireContext()).favouriteDao().getProduct(product.id!!) == null) {
                    NatureDb.getInstance(requireContext()).favouriteDao().insertProduct(product)
                    mBinding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_fav_add))
                    Constants.getTranslate(requireContext(), "add_to_fav")
                } else {
                    NatureDb.getInstance(requireContext()).favouriteDao().removeProduct(product.id!!)
                    mBinding.favouriteImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_fav))
                    Constants.getTranslate(requireContext(), "remove_from_fav")
                }
                EventBus.getDefault().postSticky(FavoritesUpdatedEvent())
                EventBus.getDefault().postSticky(updateItemCount())
                val dialog = DialogCustom(requireContext(), R.drawable.ic_add_fav, dialogMsg)
                dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
                dialog.showDialog()
            }
        }
        mBinding.incrementBtn.setOnClickListener {
            val count = mBinding.itemCountTv.text.toString().toFloat()
                addToCart(mBinding.incrementBtn.context, product.id!!, count + factorIncrement)
        }
        mBinding.decrementBtn.setOnClickListener {
            val count = mBinding.itemCountTv.text.toString().toFloat()
            if (count > factorIncrement) {
                addToCart(mBinding.incrementBtn.context, product.id!!, count - factorIncrement)
            } else if (count == factorIncrement) {
                deleteFromCart(Persister.with(requireContext()).getCartItemId(product.id))
            }
        }
    }

    private fun deleteFromCart(itemId: Long?) {
        CartService(removeFromCartRc, object : Results {
            override fun onSuccess(requestCode: Int, data: String) {
                val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                Persister.with(requireContext()).persist(Constants.cartPersistenceKey, data)
                EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                EventBus.getDefault().postSticky(updateItemCount())
                val count = mBinding.itemCountTv.text.toString().toFloat()
                showItemCountText(mBinding.itemCountTv, (count - factorIncrement), factorIncrement)
                EventBus.getDefault().postSticky(AdapterNotifyEvent())

                mBinding.totalPriceLabel.visibility = View.GONE
            }

            override fun onFailure(requestCode: Int, data: String) {
                showToast(data)
            }
        }).removeFromCart(itemId ?: 0)
    }

    private fun addToCart(context: Context, itemId: Long, quantity: Float) {

        cartID = PreferenceManager.getDefaultSharedPreferences(context).getLong(Constants.cartID, 0)
        CartService(addToCartRequest, object : Results {
            override fun onSuccess(requestCode: Int, data: String) {
                val cartDetail: CartDetail = Gson().fromJson(data, CartDetail::class.java)
                PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(Constants.cartID, cartDetail.id!!).apply()
                Persister.with(requireContext()).persist(Constants.cartPersistenceKey, data)
                showItemCountText(mBinding.itemCountTv, quantity, factorIncrement)
                updateTotalAmount()
                EventBus.getDefault().postSticky(updateItemCount())
                EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                EventBus.getDefault().postSticky(CartItemAddedEvent(cartDetail.items?.size ?: 0, cartDetail.subTotal))
                EventBus.getDefault().postSticky(AdapterNotifyEvent())

            }

            override fun onFailure(requestCode: Int, data: String) {
                showToast(data)
            }
        }).addToCart(itemId, quantity, cartID)
    }

    private fun updateTotalAmount() {
        mBinding.totalPriceLabel.visibility = View.VISIBLE
        mBinding.totalPriceLabel.text = getString(R.string.aed_total_price, Persister.with(requireContext()).getCartItemAmount(product.id).toString())
    }

    private fun showItemCountText(textView: TextView, value: Float, factor: Float) {
        var afterDigit = 1
        if (factor > 0.5)
            afterDigit = 0
        if (value == 0f)
            afterDigit = 0

        textView.text = (String.format("%.$afterDigit" + "f", value))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

}