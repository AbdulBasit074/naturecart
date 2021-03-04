package com.example.naturescart.helper

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

class Constants {
    companion object {
        const val splashTime: Long = 300
        const val cartID: String = "cartID"
        const val userID: String = "userID"
        const val dataPassKey = "dataPassKey"
        const val addressID: String = "addressID"
        const val orderDetail: String = "orderDetail"
        const val onBoardingShow: String = "onBoardingShow"
        const val languageSelected = "languageSelected"
        const val categoryID = "categoryID"
        const val categoryName = "categoryName"
        const val cartDetail: String = "cartDetail"
        const val fcmTokenPersistenceKey = "fcmTokenPersistenceKey"
        const val baseUrl: String = "https://app.naturescart.ae/api/"
        const val locationDialogRequestKey = 6518
        var selectAddressId: Int = 0
        const val isAddressSelection = "isAddressSelection"
        const val isUpdate = "isUpdate"
        const val paymentMethodUrl = "https://app.naturescart.ae/paytabs_payment/"
        const val selectionAddress = "selectionAddress"
        const val updateAddress = "updateAddress"
        var fcmToken = "489384h3bf4348hf4893023dh83h20h"
        const val deviceType = "android"

        fun geoCoding(latitude: Double, longitude: Double, context: Context): String {
            val addresses: List<Address>
            val geoCoder = Geocoder(context, Locale.getDefault())

            addresses = geoCoder.getFromLocation(
                latitude,
                longitude,
                1
            )
            val address = addresses[0]
                .getAddressLine(0)
            if (address.isNotEmpty()) {
                address.substring(0, address.length - 1)
            }
            return address.toString()
        }
    }


}