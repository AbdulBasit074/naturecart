package com.example.naturescart.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.adapters.AddressesRvAdapter
import com.example.naturescart.databinding.ActivityAddressBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.Address
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.address.AddressService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AddressActivity : AppCompatActivity(), Results {


    companion object {
        fun newInstance(context: Context, isSelection: Boolean): Intent {
            return Intent(
                context,
                AddressActivity::class.java
            ).putExtra(Constants.isAddressSelection, isSelection)
        }
    }

    private var listAddress: ArrayList<Address> = ArrayList()
    private var isSelection: Boolean = false
    private val addressList: Int = 2223
    private val positionNick: Int = 0
    private val addRequest: Int = 32
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val updateRequest: Int = 22
    private var addressSelect: Address? = null
    private lateinit var dialogShow: DialogCustom
    private var loggedUser: User? = null
    private lateinit var loading: LoadingDialog

    private lateinit var binding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address)
        loading = LoadingDialog(this)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        isSelection = intent.getBooleanExtra(Constants.isAddressSelection, false)
        setListener()
        loading.show()
        AddressService(addressList, this).getAddress(loggedUser?.accessToken ?: "")
        setData()
        handlerSet()

    }

    private fun handlerSet() {
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            dialogShow.dismiss()
        }
    }

    private fun setData() {
        if (isSelection)
            binding.title.text = Constants.getTranslate(this, "select_address")
    }

    private fun setListener() {

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.addNew.setOnClickListener {
            moveForResult(AddNewAddress::class.java, addRequest)
        }
    }

    private fun setAdapters() {

        if (listAddress.isNotEmpty())
            addressSelect = listAddress[0]

        binding.addressRv.adapter = AddressesRvAdapter(listAddress, isSelection) { data -> addressIs(data) }
    }

    private fun addressIs(data: Address) {
        if (isSelection) {
            addressSelect = data
            onBackPressed()
        } else
            moveForResult(AddNewAddress.newInstance(this, true, data), updateRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                updateRequest -> {
                    dialogShow = DialogCustom(this, R.drawable.ic_thumb, Constants.getTranslate(this, "address_update"))
                    dialogShow.show()
                    handler.postDelayed(runnable, 1000)
                    AddressService(addressList, this).getAddress(loggedUser?.accessToken ?: "")
                }
                addRequest -> {
                    dialogShow = DialogCustom(this, R.drawable.ic_thumb, Constants.getTranslate(this, "address_added"))
                    dialogShow.show()
                    handler.postDelayed(runnable, 1000)
                    AddressService(addressList, this).getAddress(loggedUser?.accessToken ?: "")
                }
            }
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        loading.dismiss()
        when (requestCode) {
            addressList -> {
                listAddress.clear()
                listAddress = Gson().fromJson(data, object : TypeToken<ArrayList<Address>>() {}.type)
                setAdapters()
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        loading.dismiss()
        showToast(data)
    }

    override fun onBackPressed() {
        if (addressSelect == null) {
            finish()
        } else {
            val resultIntent = Intent()
            resultIntent.putExtra(Constants.selectionAddress, addressSelect)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

}