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


    companion object {
        fun newInstance(context: Context, isUpdate: Boolean): Intent {
            return Intent(context, AddNewAddress::class.java).putExtra(Constants.isUpdate, isUpdate)
        }
    }


    private lateinit var binding: ActivityAddNewAddressBinding
    private val autocompleteRequestCode = 62839

    private var latLng: LatLng? = null
    private var fields = arrayListOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
    private var mMap: GoogleMap? = null
    private val citiesRequest: Int = 1222
    private val nickRequestGet: Int = 1112
    private val addressAddRequest: Int = 1412
    private val addressUpdateRequest: Int = 4412
    private var positionNick: Int = 0

    private var citiesList: ArrayList<String> = ArrayList()
    private var nickList: ArrayList<String> = ArrayList()
    private var nickListObject: ArrayList<NickAddress> = ArrayList()
    private var isUpdate: Boolean = false
    private lateinit var loggedUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_address)
        loggedUser = NatureDb.newInstance(this).userDao().getLoggedUser()
        isUpdate = intent.getBooleanExtra(Constants.isUpdate, false)

        AddressService(nickRequestGet, this).getNickAddress(loggedUser.accessToken)
        AddressService(citiesRequest, this).getCities(loggedUser.accessToken)
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
        }
    }

    private fun setListeners() {

        binding.addressNickEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (nickListObject.get(positionNick).key == "other")
                    binding.addressNickOther.visibility = View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.addressAddBtn.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.addressNickEt.setOnClickListener {
            setDataList("Select Nick", nickList, binding.addressNickEt)
        }
        binding.cityEt.setOnClickListener {
            setDataList("Select City", citiesList, binding.cityEt)
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
                for (i in 0 until nickListObject.size - 1) {
                    nickList.add(nickListObject[i].type!!)
                }

            }
            addressAddRequest -> {

            }
            addressUpdateRequest -> {
            }
        }
    }

    private fun setDataList(title: String, list: ArrayList<String>, textView: TextView) {
        val builder = AlertDialog.Builder(this)
        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        builder.setAdapter(adapter) { _, which ->
            textView.text = list[which]
            positionNick = which
        }
        val dialog = builder.create()
        dialog.setCustomTitle(binding.root.context.customTextView(title))
        dialog.show()
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }
}