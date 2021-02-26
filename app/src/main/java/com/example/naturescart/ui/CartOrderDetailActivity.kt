package com.example.naturescart.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityCartOrderDetailBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.moveForResult
import com.example.naturescart.helper.moveTo
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.Address
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.address.AddressService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartOrderDetailActivity : AppCompatActivity(), Results {


    companion object {
        fun newInstance(context: Context, cartDetail: CartDetail): Intent {
            return Intent(
                context,
                CartOrderDetailActivity::class.java
            ).putExtra(Constants.cartDetail, cartDetail)

        }
    }


    private var loggedUser: User? = null
    private val addressList: Int = 2223
    private val paymentMethodRequest: Int = 1122
    private var cartDetail: CartDetail? = null
    private lateinit var binding: ActivityCartOrderDetailBinding
    private var listAddress: ArrayList<Address> = ArrayList()

    private var addressSelect: com.example.naturescart.model.Address =
        com.example.naturescart.model.Address()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_order_detail)
        loggedUser = NatureDb.newInstance(this).userDao().getLoggedUser()
        cartDetail = intent.getParcelableExtra(Constants.cartDetail)
        setCartDetail()
        AddressService(addressList, this).getAddress(loggedUser!!.accessToken)
        setListener()
    }

    private fun setCartDetail() {
        if (cartDetail != null) {
            binding.bottomSheetCO.itemCharges.text = cartDetail!!.subTotal.toString()
            binding.bottomSheetCO.deliveryCharges.text =
                cartDetail!!.summary!!.deliveryChanges.toString()
            binding.bottomSheetCO.totalCharges.text = cartDetail!!.summary!!.subTotal.toString()
        }
    }

    private fun setListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.selectedAddressContainer.setOnClickListener {
            startActivityForResult(AddressActivity.newInstance(this, true), 0)
        }
        binding.bottomSheetCO.Btn.setOnClickListener {
            moveForResult(
                PaymentWebView.newInstance(
                    this, cartDetail?.id!!, loggedUser!!.id,
                    addressSelect.id!!
                ), paymentMethodRequest
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            0 -> {
                if (data != null) {
                    addressSelect = data!!.getParcelableExtra(Constants.selectionAddress)!!
                    binding.addressTitle.text = addressSelect.addressNick
                    binding.addressDetail.text = addressSelect.address

                }
            }
            paymentMethodRequest -> {
                if (resultCode == Activity.RESULT_OK) {
                    moveTo(OrderPlacedActivity::class.java)
                }
            }

        }

    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            addressList -> {
                listAddress.clear()
                listAddress =
                    Gson().fromJson(data, object : TypeToken<ArrayList<Address>>() {}.type)
                addressSelect = listAddress[0]
                binding.addressTitle.text = addressSelect.addressNick
                binding.addressDetail.text = addressSelect.address
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }

}