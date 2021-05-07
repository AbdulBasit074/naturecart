package com.example.naturescart.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityAddNewAddressBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.Address
import com.example.naturescart.model.NickAddress
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.address.AddressService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class AddNewAddress : AppCompatActivity(), OnMapReadyCallback, Results {

    private lateinit var binding: ActivityAddNewAddressBinding
    private var latLng: LatLng? = null
    private var fields = arrayListOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
    private var mMap: GoogleMap? = null
    private val areaRequest: Int = 1222
    private val addressAddRequest: Int = 1412
    private val addressUpdateRequest: Int = 4412
    private val markAddressOnMapRc = 3894
    private var positionNickKey: String = ""
    private var addressTypeSelect: String? = null
    private var addressSave: Address = Address()
    private var areaList: ArrayList<String> = ArrayList()
    private var areaSearchList: ArrayList<AreaSearchAble> = ArrayList()
    private var isUpdate: Boolean = false
    private var loggedUser: User? = null

    companion object {
        fun newInstance(context: Context, isUpdate: Boolean, addressUpdate: Address): Intent {
            return Intent(context, AddNewAddress::class.java).putExtra(Constants.isUpdate, isUpdate)
                .putExtra(Constants.updateAddress, addressUpdate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_address)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        isUpdate = intent.getBooleanExtra(Constants.isUpdate, false)
        showToast(Constants.getTranslate(this, "loading_map"))
        resetAddressTypeView()
        AddressService(areaRequest, this).getCities(loggedUser?.accessToken ?: "")
        setListeners()
        updateUI()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (!isGpsEnable()) {
            askToEnableGPS { requestCode, resultCode, data -> onActivityResult(requestCode, resultCode, data) }
        } else {
            checkPermissionAndGetLocation()
        }

    }

    private fun updateUI() {
        if (isUpdate) {
            addressSave = intent.getParcelableExtra(Constants.updateAddress)!!
            if (addressSave.addressNick.equals("Home") || addressSave.addressNick.equals("الرئيسية"))
                binding.home.performClick()
            if (addressSave.addressNick.equals("Work") || addressSave.addressNick.equals("عمل"))
                binding.work.performClick()
            if (addressSave.addressNick.equals("Other") || addressSave.addressNick.equals("آخر"))
                binding.work.performClick()

            addressTypeSelect = addressSave.addressNick
            var phoneNo = addressSave.phone!!.removePrefix("+971")
            binding.phoneNo.setText(phoneNo)
            binding.cityEt.text = addressSave.city
            binding.firstNameEt.setText(addressSave.firstName.toString())
            binding.lastNameEt.setText(addressSave.lastName.toString())
            binding.addressAddBtn.text = Constants.getTranslate(this, "update")
            positionNickKey = addressSave.addressNick.toString().toLowerCase()
            binding.areaEt.text = addressSave.area
            binding.nearEt.setText(addressSave.nearestLandmark.toString())
            binding.buildingNameEt.setText(addressSave.buildingName)
            binding.villaApartment.setText(addressSave.apartment)
            if (addressSave.defaultAddress)
                binding.defaultAddress.isChecked = true

        } else {
            binding.home.performClick()
            addressTypeSelect = Constants.getTranslate(this, "home")
            binding.phoneNo.setText(loggedUser!!.phone.removePrefix("+971"))
            binding.firstNameEt.setText(loggedUser!!.firstName)
            binding.lastNameEt.setText(loggedUser!!.lastName)
            binding.addressAddBtn.text = Constants.getTranslate(this, "add_new_address")
        }
    }

    private fun setListeners() {


        binding.areaEt.setOnClickListener {

            var custom = com.example.naturescart.helper.SimpleSearchDialogCompat(
                this,
                Constants.getTranslate(this, "select_area"),
                Constants.getTranslate(this, "search"),
                null,
                areaSearchList
            ) { dialog, item, position ->
                binding.areaEt.text = item.title
                dialog.dismiss()
            }
            custom.show()
        }
        binding.work.setOnClickListener {
            setAddressTypeSelector(binding.work)
        }
        binding.home.setOnClickListener {
            setAddressTypeSelector(binding.home)
        }
        binding.other.setOnClickListener {
            setAddressTypeSelector(binding.other)
        }
        binding.clickOnMapLabel.setOnClickListener {
            startActivityForResult(AddressOnMapActivity.newInstance(this, latLng), markAddressOnMapRc)
        }
        binding.mapView.setOnClickListener {
            startActivityForResult(AddressOnMapActivity.newInstance(this, latLng), markAddressOnMapRc)
        }


//        binding.mapView.setOnLongClickListener(object : View.OnLongClickListener {
//            override fun onLongClick(v: View?): Boolean {
//                startActivityForResult(AddressOnMapActivity.newInstance(this, latLng), markAddressOnMapRc)
//                return true
//            }
//        })
//
        binding.mapView.setOnLongClickListener {
            startActivityForResult(AddressOnMapActivity.newInstance(this, latLng), markAddressOnMapRc)
            return@setOnLongClickListener true
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.addressAddBtn.setOnClickListener {
            if (isInputOk()) {
                addressSave.addressNick = addressTypeSelect
                addressSave.city = binding.cityEt.text.toString()
                addressSave.firstName = binding.firstNameEt.text.toString()
                addressSave.lastName = binding.lastNameEt.text.toString()
                addressSave.area = binding.areaEt.text.toString()
                addressSave.buildingName = binding.buildingNameEt.text.toString()
                addressSave.apartment = binding.villaApartment.text.toString()
                addressSave.phone = getString(R.string.country_phone_code) + binding.phoneNo.text
                addressSave.nearestLandmark = binding.nearEt.text.toString()
                addressSave.defaultAddress = binding.defaultAddress.isChecked
                if (latLng == null) {
                    showToast(Constants.getTranslate(this, "map_location_not_updated"))
                } else {
                    addressSave.latitude = latLng!!.latitude
                    addressSave.longitude = latLng!!.longitude
                    if (isUpdate)
                        AddressService(addressUpdateRequest, this).updateAddress(loggedUser?.accessToken ?: "", addressSave, addressSave.id!!)
                    else
                        AddressService(addressAddRequest, this).addAddress(loggedUser?.accessToken ?: "", addressSave)
                }
            }
        }
        binding.cityEt.setOnClickListener {
            binding.cityEt.showOrDismiss()
        }
    }

    private fun setAddressTypeSelector(textView: TextView) {
        resetAddressTypeView()
        addressTypeSelect = textView.text.toString()
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.saleem_green))
    }

    private fun isInputOk(): Boolean {

        when {
            addressTypeSelect.isNullOrEmpty() -> {
                showToast(Constants.getTranslate(this, "nick_address_req"))
                return false
            }
            binding.areaEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "area_field_required"))
                return false
            }
            binding.buildingNameEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "building_name_required"))
                return false
            }
            binding.firstNameEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "first_name_req"))
                return false
            }
            binding.lastNameEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "last_name_req"))
                return false
            }
            binding.villaApartment.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "villa_aparment_required"))
                return false
            }
            binding.phoneNo.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "phone_field_req"))
                return false
            }
            binding.cityEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "city_field_req"))
                return false
            }
            else -> {
                return true
            }
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        if (latLng == null && isGpsEnable())
            checkPermissionAndGetLocation()

        mMap?.setOnCameraMoveStartedListener {
            binding.mapView.requestDisallowInterceptTouchEvent(true)

        }
        mMap?.setOnCameraIdleListener {
            binding.mapView.requestDisallowInterceptTouchEvent(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.locationDialogRequestKey && resultCode == Activity.RESULT_OK) {
            checkPermissionAndGetLocation()
        } else if (requestCode == markAddressOnMapRc && resultCode == RESULT_OK) {
            latLng = data?.getParcelableExtra(Constants.dataPassKey)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
    }

    private fun checkPermissionAndGetLocation() {
        /*** check permission and get current location**/

        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    //getting current location
                    val s = fields
                    getCurrentLocation(fields) { place -> onLocationAvailable(place) }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun onLocationAvailable(place: Place) {
        /*** On getting location**/
        latLng = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        mMap?.clear()
        mMap!!.setOnCameraMoveListener { latLng = mMap!!.cameraPosition.target }
        mMap?.setOnCameraIdleListener {
            val currentZoom: Float = (mMap?.cameraPosition?.zoom ?: 15f)
            if (currentZoom < 13.5f) {
                mMap?.clear()
            }

        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            areaRequest -> {
                areaList = Gson().fromJson(data, object : TypeToken<ArrayList<String>>() {}.type)
                areaList.forEach {
                    areaSearchList.add(AreaSearchAble(it))
                }

                binding.cityEt.setItems(R.array.country)
                binding.cityEt.selectItemByIndex(0)
            }
            addressAddRequest -> {
                // DialogCustom(this, R.drawable.ic_thumb, data).show()
                setResult(Activity.RESULT_OK)
                finish()

            }
            addressUpdateRequest -> {
                //DialogCustom(this, R.drawable.ic_thumb, data).show()
                setResult(Activity.RESULT_OK)
                finish()

            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }

    private fun resetAddressTypeView() {
        binding.home.background = ContextCompat.getDrawable(this, R.drawable.border_address_type)
        binding.work.background = ContextCompat.getDrawable(this, R.drawable.border_address_type)
        binding.other.background = ContextCompat.getDrawable(this, R.drawable.border_address_type)
        binding.home.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.work.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.other.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

}