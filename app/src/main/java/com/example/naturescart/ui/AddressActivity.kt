package com.example.naturescart.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.adapters.AddressessRvAdapter
import com.example.naturescart.databinding.ActivityAddressBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.DialogCustom
import com.example.naturescart.helper.moveForResult


class AddressActivity : AppCompatActivity() {


    companion object {
        fun newInstance(context: Context, isSelection: Boolean): Intent {
            return Intent(
                context,
                AddressActivity::class.java
            ).putExtra(Constants.isAddressSelection, isSelection)
        }
    }


    private var list: ArrayList<String> = ArrayList()
    private var isSelection: Boolean = false
    private lateinit var binding: ActivityAddressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address)
        isSelection = intent.getBooleanExtra(Constants.isAddressSelection, false)
        setData()
        setAdapters()
        setListener()

    }

    private fun setData() {
        list.add("Dummy")
        list.add("Dummy")
        list.add("Dummy")
        if (isSelection)
            binding.title.text = "Select Address"

    }

    private fun setListener() {

        binding.backBtn.setOnClickListener {
            onBackPressed()

        }
        binding.addNew.setOnClickListener {
            moveForResult(AddNewAddress::class.java, 0)
        }

    }

    private fun setAdapters() {
        binding.addressRv.adapter = AddressessRvAdapter(list, isSelection)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    DialogCustom(
                        this,
                        R.drawable.ic_thumb,
                        "Address Added"
                    ).show()
                }


            }


        }


    }


}