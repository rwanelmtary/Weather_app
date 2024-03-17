package com.example.weather.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.DB.FavLocationDB
import com.example.weather.DB.LocalDataSourceImp
import com.example.weather.Model.WeatherModelFactory
import com.example.weather.Model.WeatherViewModel
import com.example.weather.Pojo.FavoriteLocation
import com.example.weather.Pojo.RepoImp
import com.example.weather.R
import com.example.weather.network.RemoteDataSourceImp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: WeatherViewModel
    private lateinit var locationDatabase: FavLocationDB

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        getCurrentLocation()
        setMapClickListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var localDataSource = LocalDataSourceImp(requireContext())
        val remoteDataSource = RemoteDataSourceImp()
        val modelFactory by lazy { WeatherModelFactory(RepoImp.getInstance(remoteDataSource,localDataSource),requireContext()) }
        viewModel = ViewModelProvider(this, modelFactory).get(WeatherViewModel::class.java)

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationDatabase = FavLocationDB.getInstance(requireContext())
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
                    map.addMarker(MarkerOptions().position(currentLatLng).title("Your Location"))
                }
            }
    }

    private fun setMapClickListener() {
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Chosen Location"))
            showSaveLocationDialog(latLng.latitude, latLng.longitude)
        }
    }

    private fun showSaveLocationDialog(latitude: Double, longitude: Double) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Save Location")

        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.addfav, null)

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        val country: String? = addresses?.getOrNull(0)?.countryName
        val city: String? = addresses?.getOrNull(0)?.locality

        builder.setView(dialogView)

        builder.setPositiveButton("Save") { dialogInterface: DialogInterface, i: Int ->
            if (country != null && city != null) {
                val locationData = FavoriteLocation(
                    latitude = latitude,
                    longitude = longitude,
                    address_en = "$city, $country",
                    address_ar = "$city, $country"
                )
                insertLocationIntoDB(locationData)
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun insertLocationIntoDB(locationData: FavoriteLocation) {
        GlobalScope.launch(Dispatchers.IO) {
            locationDatabase.getLocationDao().insertFavLocation(locationData)
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val DEFAULT_ZOOM = 15f
    }
}
