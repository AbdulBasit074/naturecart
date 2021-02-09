package com.example.naturescart.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityAddNewAddressBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.askToEnableGPS
import com.example.naturescart.helper.getCurrentLocation
import com.example.naturescart.helper.isGpsEnable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class AddNewAddress : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var binding: ActivityAddNewAddressBinding
    private val autocompleteRequestCode = 62839

    private var latLng: LatLng? = null
    private var fields = arrayListOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
    private var mMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_address)
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

    private fun setListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.addressAddBtn.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
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
                    val s  = fields
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
}