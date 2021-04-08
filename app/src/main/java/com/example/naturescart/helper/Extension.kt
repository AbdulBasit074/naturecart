package com.example.naturescart.helper


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.naturescart.BuildConfig
import com.example.naturescart.R
import com.example.naturescart.model.Category
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.iid.FirebaseInstanceId
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


fun AppCompatActivity.moveTo(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

fun AppCompatActivity.moveToWithoutHistory(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
    finishAffinity()
}

fun AppCompatActivity.moveToPlayStore() {
    val uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            )
        )
    }
}

fun AppCompatActivity.setLanguage() {
    val displayMetrics = resources.displayMetrics
    val configuration = resources.configuration
    configuration.setLocale(Locale(TranslationsHelper.getInstance(this).getLocale()))
    resources.updateConfiguration(configuration, displayMetrics)
}

fun Fragment.moveFromFragment(activity: Activity, clazz: Class<*>) {
    startActivity(Intent(activity, clazz))
}

fun Fragment.moveForResultFragment(activity: Activity, clazz: Class<*>, requestCode: Int) {
    startActivityForResult(Intent(activity, clazz), requestCode)
}

fun AppCompatActivity.hasStoragePermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

fun AppCompatActivity.requestedStoragePermission(requestCode: Int) {
    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(this, permission, requestCode)
}

fun Fragment.moveFromFragment(intent: Intent) {
    startActivity(intent)
}


fun AppCompatActivity.moveToIntent(intent: Intent) {
    startActivity(intent)
}


fun AppCompatActivity.moveForResult(clazz: Class<*>, requestCode: Int) {
    startActivityForResult(Intent(this, clazz), requestCode)
}

fun AppCompatActivity.moveForResult(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
}

fun AppCompatActivity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}


fun Context.customTextView(text: String): TextView {
    val textView = TextView(this)
    textView.setTextColor(getColor(R.color.black))
    textView.text = text
    textView.setPadding(20, 30, 20, 30)
    textView.textSize = 20f
    textView.setTextColor(getColor(R.color.white))
    textView.setBackgroundColor(getColor(R.color.saleem_green))
    return textView
}

fun AppCompatActivity.isGpsEnable(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
}

/**
 * Enable Gps Location
 * */
fun AppCompatActivity.askToEnableGPS(onActivityResult: (Int, Int, Intent?) -> Unit) {
    val locationRequest = LocationRequest.create()?.apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest!!)
    builder.setAlwaysShow(true)

    val client: SettingsClient = LocationServices.getSettingsClient(this)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        onActivityResult(Constants.locationDialogRequestKey, Activity.RESULT_OK, null)
    }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                exception.startResolutionForResult(
                    this,
                    Constants.locationDialogRequestKey
                )
            } catch (sendEx: IntentSender.SendIntentException) {
                onActivityResult(Constants.locationDialogRequestKey, Activity.RESULT_CANCELED, null)
            }
        }
    }
}

fun convertDate(dateString: String?): String {
    val sourceSdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
    sourceSdf.timeZone = TimeZone.getTimeZone("GMT")
    val destSdf = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
    val date = sourceSdf.parse(dateString ?: "")!!
    return destSdf.format(date)
}

fun AppCompatActivity.getCurrentLocation(fields: ArrayList<Place.Field>, onLocationAvailable: (Place) -> Unit) {
    if (!Places.isInitialized())
        Places.initialize(applicationContext, getString(R.string.google_maps_api), Locale.getDefault())
    val request = FindCurrentPlaceRequest.newInstance(fields)
    val client = Places.createClient(this)
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return
    } else {
        val placeResponse = client.findCurrentPlace(request)
        placeResponse.addOnCompleteListener {
            if (it.isSuccessful) {
                val response = it.result
                var position = 0
                var highestLikelihood = 0.0
                for (i in 0 until response?.placeLikelihoods!!.size) {
                    if (response.placeLikelihoods[i].likelihood > highestLikelihood) {
                        highestLikelihood = response.placeLikelihoods[i].likelihood
                        position = i
                    }
                }
                val place = response.placeLikelihoods[position].place
                onLocationAvailable(place)
            }
        }
    }
}

fun Array<String?>.convertToString(): String {
    val sb = StringBuilder()
    for (string in this) {
        sb.append(string)
    }
    return sb.toString()
}

fun Context.showKeyboard() {
    try {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AppCompatActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun ArrayList<Category>.containsWithThisName(name: String): Boolean {
    forEach {
        if (it.name == name)
            return true
    }
    return false
}

fun AppCompatActivity.checkAndFetchFcmToken() {
    if (Persister.with(this).getPersisted(Constants.fcmTokenPersistenceKey) == null) {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            Persister.with(this).persist(Constants.fcmTokenPersistenceKey, token)
            Log.d("TAGEE", token)
        }
    } else {
        Log.d("TAGEE", Persister.with(this).getPersisted(Constants.fcmTokenPersistenceKey)!!)
    }


}