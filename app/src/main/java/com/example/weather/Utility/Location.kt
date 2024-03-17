package com.example.weather.Utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.log

const val PERMISSION_ID = 9
class LocationUtils(private val context: Context) {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private var isChoosen = false
    private var locationListener: ((Location) -> Unit)? = null

    init {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        geocoder = Geocoder(context, Locale.getDefault())
    }

    fun setLocationListener(listener: (Location) -> Unit) {
        locationListener = listener
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (!isChoosen){
                isChoosen = true
                val mLastLocation: Location = locationResult.lastLocation
                locationListener?.invoke(mLastLocation)
                Log.i("TAG", "onLocationResult: " + mLastLocation.latitude + " " + mLastLocation.longitude)
            }

        }
    }

    fun checkPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        return result
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun requestLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                requestLocationUpdates()
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            context as AppCompatActivity,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun sendLocation(phoneNumber: String, messageBody: String) {
        val smsUri = Uri.parse("smsto:$phoneNumber")
        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
        smsIntent.putExtra("sms_body", messageBody)
        context.startActivity(smsIntent)
    }
    fun getAddress(location: Location, callback: (String?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses?.firstOrNull()
            val fullAddress = StringBuilder()

            // Append each part of the address
            address?.apply {
//                fullAddress.appendLine(if (subThoroughfare != null) subThoroughfare else "")
//                fullAddress.appendLine(if (thoroughfare != null) thoroughfare else "")
//                fullAddress.appendLine(if (subLocality != null) subLocality else "")
                fullAddress.appendLine(if (locality != null) locality else "")
                fullAddress.appendLine(if (adminArea != null) adminArea else "")
              //  fullAddress.appendLine(if (postalCode != null) postalCode else "")
                fullAddress.appendLine(if (countryName != null) countryName else "")
            }

            callback(fullAddress.toString().trim())
        }
    }


}
