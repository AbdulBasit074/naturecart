package com.example.naturescart.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.DeliveryDateDailogBinding
import com.example.naturescart.model.DeliveryDateTime
import com.example.naturescart.services.Results
import com.example.naturescart.services.cart.CartService
import com.google.gson.Gson

class DialogDeliveryDate(context: Context, private var cartId: Long?, private var dataDateTime: String, private var callBack: (DeliveryDateTime.Date, DeliveryDateTime.TimeSlot, String) -> Unit) :
    Dialog(context), Results {

    private lateinit var binding: DeliveryDateDailogBinding
    private var startSetRequest: Int = 2211
    private var getTimeRequest: Int = 2421
    private var addDateTimeRequest: Int = 2721
    private var dateSelect: DeliveryDateTime.Date? = DeliveryDateTime.Date()
    private var timeSelect: DeliveryDateTime.TimeSlot? = DeliveryDateTime.TimeSlot()
    private var loading: LoadingDialog? = null
    private var da: DeliveryDateTime.TimeSlot? = null
    private var dateTimeSets: DeliveryDateTime = DeliveryDateTime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.delivery_date_dailog, null, false)
        this.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        setContentView(binding.root)
        initView()
        setListeners()
    }

    private fun setListeners() {

        binding.okBtn.setOnClickListener {
           timeSelect!!.time =  timeSelect?.time?.replace("Between", "")
            CartService(addDateTimeRequest, this).addDeliveryDateTime(dateSelect?.dateKey!!, timeSelect?.time!!, cartId!!)
        }
        binding.cancleBtn.setOnClickListener {
            this.dismiss()
        }

    }
    private fun initView() {
        onSuccess(startSetRequest, dataDateTime)
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            startSetRequest -> {
                loading?.dismiss()
                dateTimeSets = Gson().fromJson(data, DeliveryDateTime::class.java)
                dateSelect = dateTimeSets.dates[0]
                timeSelect = dateTimeSets.timeSlots[0]
                setDateSpinnerValues()
                setTimeSpinnerValues()
            }
            getTimeRequest -> {
                dateTimeSets = Gson().fromJson(data, DeliveryDateTime::class.java)
                timeSelect = dateTimeSets.timeSlots[0]
                setTimeSpinnerValues()
            }
            addDateTimeRequest -> {
                callBack(dateSelect!!, timeSelect!!, data)
                dismiss()
            }
        }
    }

    private fun setDateTimeList() {

    }

    override fun onFailure(requestCode: Int, data: String) {
        context.showToast(data)
    }


    private fun getAllDates(): ArrayList<String> {
        val datesList: ArrayList<String> = ArrayList()
        dateTimeSets.dates.forEach {
            datesList.add(it.date!!)
        }
        return datesList
    }

    private fun getAllTimes(): ArrayList<String> {
        val timeList: ArrayList<String> = ArrayList()
        dateTimeSets.timeSlots.forEach {
            timeList.add(it.time!!)
        }
        return timeList
    }

    private fun setTimeSpinnerValues() {

        val adapterSpinner: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item, getAllTimes())
        adapterSpinner.setDropDownViewResource(R.layout.li_drop_down)
        binding.deliveryTimeDropDown.adapter = adapterSpinner
        binding.deliveryTimeDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                timeSelect = dateTimeSets.timeSlots[position]
            }
        }
    }

    private fun setDateSpinnerValues() {
        val adapterSpinner: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item, getAllDates())
        adapterSpinner.setDropDownViewResource(R.layout.li_drop_down)
        binding.deliveryDayDropDown.adapter = adapterSpinner
        binding.deliveryDayDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dateSelect = dateTimeSets.dates[position]
                CartService(getTimeRequest, this@DialogDeliveryDate).getDeliveryDateTime(dateSelect?.dateKey)
            }
        }
    }


}