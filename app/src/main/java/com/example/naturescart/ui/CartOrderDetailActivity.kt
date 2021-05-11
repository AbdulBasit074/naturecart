package com.example.naturescart.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityCartOrderDetailBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.Address
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.DeliveryDateTime
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.address.AddressService
import com.example.naturescart.services.cart.CartService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartOrderDetailActivity : AppCompatActivity(), Results {

    private var loggedUser: User? = null
    private val addressList: Int = 2223
    private val paymentMethodRequest: Int = 1122
    private val couponApplyRequest: Int = 1342
    private val couponRemoveRequest: Int = 1142

    private var cartDetail: CartDetail? = null
    private lateinit var binding: ActivityCartOrderDetailBinding
    private var listAddress: ArrayList<Address> = ArrayList()
    private var addressSelect: Address? = null
    private var loading: LoadingDialog? = null
    private var date: DeliveryDateTime.Date? = null
    private var time: DeliveryDateTime.TimeSlot? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_order_detail)
        loading = LoadingDialog(this)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        cartDetail = Persister.with(this).getCartDetail()
        setCartDetail()
        AddressService(addressList, this).getAddress(loggedUser!!.accessToken)
        setListener()
    }

    private fun setCartDetail() {
        if (cartDetail != null) {
            binding.bottomSheetCO.vatCharges.text = getString(R.string.aed_price, String.format("%.2f", cartDetail!!.summary?.vat))
            binding.bottomSheetCO.itemCharges.text = getString(R.string.aed_price, String.format("%.2f", cartDetail!!.summary?.subTotal))
            binding.bottomSheetCO.deliveryCharges.text = getString(R.string.aed_price, String.format("%.2f", cartDetail!!.summary!!.deliveryChanges))
            binding.bottomSheetCO.totalCharges.text = getString(R.string.aed_price, String.format("%.2f", cartDetail!!.summary!!.total!!))
            if (cartDetail!!.summary!!.couponDiscount!! > 0) {
                binding.bottomSheetCO.discountAmountTitle.visibility = View.VISIBLE
                binding.bottomSheetCO.discountAmount.visibility = View.VISIBLE
                binding.bottomSheetCO.discountAmount.text = getString(R.string.aed_price, String.format("%.2f", cartDetail!!.summary?.couponDiscount))
                binding.applyDiscountCode.text = Constants.getTranslate(this, "remove")
                binding.couponCodeText.setText(cartDetail!!.summary!!.couponCode.toString())
                binding.couponCodeText.isEnabled = false
            } else {
                binding.bottomSheetCO.discountAmountTitle.visibility = View.GONE
                binding.bottomSheetCO.discountAmount.visibility = View.GONE
                binding.applyDiscountCode.text = Constants.getTranslate(this, "apply")
                binding.couponCodeText.isEnabled = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.setDeliveryDateTime.setOnClickListener {
            var dialog = DialogDeliveryDate(this, cartDetail?.id) { dateSelect, timeSlot, s ->
                binding.setDeliveryDateTime.text = dateSelect.date + "\n" + timeSlot.time
                date = dateSelect
                time = timeSlot
            }
            dialog.show()
        }
        binding.selectedAddressContainer.setOnClickListener {
            startActivityForResult(AddressActivity.newInstance(this, true), 0)
        }
        binding.applyDiscountCode.setOnClickListener {
            if (cartDetail?.summary?.couponCode!!.isNotEmpty() && cartDetail!!.summary!!.couponDiscount!! > 0) {
                loading?.show()
                CartService(couponRemoveRequest, this).removeCoupon(loggedUser!!.accessToken, binding.couponCodeText.text.toString(), cartDetail!!.id!!)
            } else {
                if (!binding.couponCodeText.text.isNullOrEmpty()) {
                    loading?.show()
                    CartService(couponApplyRequest, this).applyCoupon(loggedUser!!.accessToken, binding.couponCodeText.text.toString(), cartDetail!!.id!!)
                } else
                    showToast(Constants.getTranslate(this, "coupon_code_required"))
            }
        }
        binding.bottomSheetCO.Btn.setOnClickListener {
            if (allIsOk()) {
                moveForResult(PaymentWebView.newInstance(this, cartDetail?.id!!, loggedUser!!.id, addressSelect!!.id!!, binding.bottomSheetCO.contactLess.isChecked), paymentMethodRequest)
            }
        }
    }

    private fun allIsOk(): Boolean {
        return when {
            addressSelect == null -> {
                showToast(Constants.getTranslate(this, "select_address"))
                false
            }
            date == null -> {
                showToast(Constants.getTranslate(this, "select_delivery_time"))
                false
            }
            else -> {
                true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                if (data != null) {
                    addressSelect = data.getParcelableExtra(Constants.selectionAddress)!!
                    binding.addressTitle.text = addressSelect!!.addressNick
                    binding.addressDetail.text = addressSelect!!.buildingName + ", " + addressSelect!!.apartment
                }
            }
            paymentMethodRequest -> {
                if (resultCode == Activity.RESULT_OK) {
                    moveTo(OrderPlacedActivity::class.java)
                }
            }

        }

    }

    @SuppressLint("SetTextI18n")
    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            addressList -> {
                listAddress.clear()
                listAddress = Gson().fromJson(data, object : TypeToken<ArrayList<Address>>() {}.type)
                listAddress.forEach {
                    if (it.defaultAddress)
                        addressSelect = it
                }
                Constants.selectAddressId = addressSelect!!.id!!
                binding.addressTitle.text = addressSelect!!.addressNick
                binding.addressDetail.text = addressSelect!!.buildingName + ", " + addressSelect!!.apartment
            }
            couponApplyRequest -> {
                loading?.dismiss()
                cartDetail = Gson().fromJson(data, CartDetail::class.java)
                Persister.with(this).persist(Constants.cartPersistenceKey, data)
                setCartDetail()
            }
            couponRemoveRequest -> {
                loading?.dismiss()
                cartDetail = Gson().fromJson(data, CartDetail::class.java)
                Persister.with(this).persist(Constants.cartPersistenceKey, data)
                binding.couponCodeText.text.clear()
                setCartDetail()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        loading?.dismiss()
        showToast(data)
    }


    override fun finish() {
        Constants.selectAddressId = 0
        super.finish()
    }
}