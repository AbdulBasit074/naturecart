package com.example.naturescart.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.*

class Constants {
    companion object {
        const val splashTime: Long = 500
        const val cartID: String = "cartID"
        const val userID: String = "userID"
        const val dataPassKey = "dataPassKey"
        const val titlePassKey = "titlePassKey"
        const val addressID: String = "addressID"
        const val contactLess: String = "contactLess"
        const val dubai: String = "dubai"
        const val orderDetail: String = "orderDetail"
        const val onBoardingShow: String = "onBoardingShow"
        const val localeKey = "localeKey"
        const val languageSelected = "languageSelected"
        const val categoryID = "categoryID"
        const val categoryName = "categoryName"
        const val cartDetail: String = "cartDetail"
        const val fcmTokenPersistenceKey = "fcmTokenPersistenceKey"
        const val baseUrl: String = "https://app.naturescart.ae/api/"
        const val cartPersistenceKey = "cartPersistenceKey"
        var orderPlace: Boolean = false
        const val onBoardingPersistenceKey = "onBoardingPersistenceKey"
        const val locationDialogRequestKey = 6518
        const val categoryDetailsActivityRc = 4245
        const val searchActivityRc = 3234
        const val collectionDetailsActivityRc = 8320
        var selectAddressId: Int = 0
        const val isAddressSelection = "isAddressSelection"
        const val isUpdate = "isUpdate"
        const val paymentMethodUrl = "https://app.naturescart.ae/paytabs_payment/"
        const val termsUrl = "https://naturescart.ae/terms.html"
        const val privacyPolicyUrl = "https://naturescart.ae/privacy.html"
        const val selectionAddress = "selectionAddress"
        const val updateAddress = "updateAddress"
        const val deviceType = "android"


        fun getTranslate(context: Context, key: String): String {
            return TranslationsHelper.getInstance(context).getTranslation(key)
        }

        fun getWhatsAppUrl(context: Context): String {
            var phoneNumber = getTranslate(context, "about_phone")
            var textMsg = getTranslate(context, "whatsapp_message")
            return "https://api.whatsapp.com/send?phone=$phoneNumber&text=$textMsg"
        }


        fun geoCoding(latitude: Double, longitude: Double, context: Context): String {
            val addresses: List<Address>
            val geoCoder = Geocoder(context, Locale.getDefault())

            addresses = geoCoder.getFromLocation(latitude, longitude, 4)
            var address: String = ""
            if (addresses != null && addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
                address.substring(0, address.length - 1)
                address = address.removePrefix("Unnamed Road, ")
            }
            return address
        }

        fun geoSubLocale(latitude: Double, longitude: Double, context: Context): String {
            val addresses: List<Address>
            val geoCoder = Geocoder(context, Locale.getDefault())

            addresses = geoCoder.getFromLocation(latitude, longitude, 4)
            var address: String = ""
            if (addresses != null && addresses.isNotEmpty() && addresses[0].subLocality != null) {
                address = addresses[0].subLocality
            }
            return address
        }
        fun checkLocality(latitude: Double, longitude: Double, context: Context): String {
            val addresses: List<Address>
            val geoCoder = Geocoder(context, Locale.getDefault())
            addresses = geoCoder.getFromLocation(latitude, longitude, 4)
            var address: String = ""
            if (addresses != null && addresses.isNotEmpty() && addresses[0].locality != null) {
                address = addresses[0].locality
            }
            return address
        }



        fun setCircularImage(imageView: CircularImageView, src: String, shimmerFrameLayout: ShimmerFrameLayout) {
            shimmerFrameLayout.visibility = View.VISIBLE
            Glide.with(imageView.context).load(src).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    shimmerFrameLayout.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    shimmerFrameLayout.visibility = View.GONE
                    return false
                }

            }).into(imageView)
        }


    }


}