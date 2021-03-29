package com.example.naturescart.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AddNewAddress : AppCompatActivity(), OnMapReadyCallback, Results {

    private lateinit var binding: ActivityAddNewAddressBinding
    private val autocompleteRequestCode = 62839
    private val addRequest: Int = 32
    private val updateRequest: Int = 22

    private var latLng: LatLng? = null
    private var fields = arrayListOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
    private var mMap: GoogleMap? = null
    private val citiesRequest: Int = 1222
    private val nickRequestGet: Int = 1112
    private val addressAddRequest: Int = 1412
    private val addressUpdateRequest: Int = 4412
    private val markAddressOnMapRc = 3894
    private var positionNick: Int = 0
    private var positionNickKey: String = ""

    private var addressSave: Address = Address()
    private var citiesList: ArrayList<String> = ArrayList()
    private var nickList: ArrayList<String> = ArrayList()
    private var nickListObject: ArrayList<NickAddress> = ArrayList()
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
        AddressService(nickRequestGet, this).getNickAddress(loggedUser?.accessToken ?: "")
        AddressService(citiesRequest, this).getCities(loggedUser?.accessToken ?: "")
        updateUI()
        setListeners()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (!isGpsEnable()) {
            askToEnableGPS { requestCode, resultCode, data ->
                onActivityResult(
                    requestCode,
                    resultCode,
                    data
                )
            }

        } else {
            checkPermissionAndGetLocation()
        }

    }

    private fun updateUI() {
        if (isUpdate) {
            addressSave = intent.getParcelableExtra(Constants.updateAddress)!!
            binding.addressNickEt.text = addressSave.addressNick
            binding.addressEt.setText(addressSave.address)
            binding.phoneNo.setText(addressSave.phone?.replace("+971", ""))
            binding.cityEt.setText(addressSave.city)
            binding.addressAddBtn.text = Constants.getTranslate(this, "update")
            positionNickKey = addressSave.addressNick.toString().toLowerCase()

        } else {


        }
    }

    private fun setListeners() {

        binding.addressNickEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                positionNickKey = nickListObject[positionNick].key!!
                if (nickListObject[positionNick].key == "other")
                    binding.addressNickOther.visibility = View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        binding.markLocationOnMapBtn.setOnClickListener {
            startActivityForResult(AddressOnMapActivity.newInstance(this, latLng), markAddressOnMapRc)
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.addressAddBtn.setOnClickListener {

            if (isInputOk()) {
                addressSave.city = binding.cityEt.text.toString()
                addressSave.phone = getString(R.string.country_phone_code) + binding.phoneNo.text
                addressSave.address = binding.addressEt.text.toString()
                if (positionNickKey == "other")
                    addressSave.addressNick = binding.addressNickOther.text.toString()
                else
                    addressSave.addressNick = binding.addressNickEt.text.toString()

                addressSave.city = binding.cityEt.text.toString()
                if (latLng == null) {
                    showToast(Constants.getTranslate(this, "map_location_not_updated"))
                } else {
                    addressSave.latitude = latLng!!.latitude
                    addressSave.longitude = latLng!!.longitude
                    if (isUpdate)
                        AddressService(addressUpdateRequest, this).updateAddress(
                            loggedUser?.accessToken ?: "", addressSave,
                            addressSave.id!!
                        )
                    else
                        AddressService(addressAddRequest, this).addAddress(
                            loggedUser?.accessToken ?: "",
                            addressSave
                        )
                }
            }
        }
        binding.addressNickEt.setOnClickListener {
            setDataList(Constants.getTranslate(this, "select_nick"), nickList, binding.addressNickEt)
            binding.addressNickOther.visibility = View.GONE
        }
        binding.cityEt.setOnClickListener {
            setDataList(Constants.getTranslate(this, "select_city"), citiesList, binding.cityEt)
        }
    }

    private fun isInputOk(): Boolean {

        when {

            binding.addressNickEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "nick_address_req"))
                return false
            }
            binding.addressEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "address_field_req"))
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
            positionNickKey == "other" -> {
                if (binding.addressNickOther.text.isEmpty()) {
                    showToast(Constants.getTranslate(this, "nick_other_req"))
                    return false
                }
                return true
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
            citiesRequest -> {
                citiesList = Gson().fromJson(data, object : TypeToken<ArrayList<String>>() {}.type)
            }
            nickRequestGet -> {
                nickListObject =
                    Gson().fromJson(data, object : TypeToken<ArrayList<NickAddress>>() {}.type)
                for (i in 0 until nickListObject.size) {
                    nickList.add(nickListObject[i].type!!)
                }

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

    private fun setDataList(title: String, list: ArrayList<String>, textView: TextView) {
        val builder = AlertDialog.Builder(this)
        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        builder.setAdapter(adapter) { _, which ->
            positionNick = which
            textView.text = list[which]
        }
        val dialog = builder.create()
        dialog.setCustomTitle(binding.root.context.customTextView(title))
        dialog.show()
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}