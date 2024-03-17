package com.example.weather.Home

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.DB.LocalDataSourceImp
import com.example.weather.Model.WeatherModelFactory
import com.example.weather.Model.WeatherViewModel
import com.example.weather.Pojo.RepoImp
import com.example.weather.Pojo.WeatherResponse
import com.example.weather.R
import com.example.weather.Utility.LocationUtils
import com.example.weather.Utility.Utility
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.network.RemoteDataSourceImp
import com.example.weather.settings.PreferenceManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.time.Duration.Companion.milliseconds

class HomeFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var locationUtils: LocationUtils
    private lateinit var cityTextView: TextView
    private lateinit var tempTextView: TextView
    private lateinit var currentTextView: TextView
    private lateinit var pressureText: TextView
    private lateinit var humidity: TextView
    private lateinit var windText: TextView
    private var long: Double = 0.0
    private var lat: Double = 0.0

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dayAdapter: DayAdapter

    private var lastStoredResponse: WeatherResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val shared: PreferenceManager? = null
        val localDataSource = LocalDataSourceImp(requireContext())
        val remoteDataSource = RemoteDataSourceImp()
        val modelFactory by lazy { WeatherModelFactory(RepoImp.getInstance(remoteDataSource, localDataSource), requireContext()) }
        viewModel = ViewModelProvider(this, modelFactory).get(WeatherViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        locationUtils = LocationUtils(requireContext())

        locationUtils.setLocationListener { location ->
            val lat = location.latitude
            val long = location.longitude
            val lang = shared?.getSelectedLanguage(requireContext())
            val unit = shared?.getSelectedTemperatureUnit(requireContext())

            viewModel.getDayWeather(lat, long, lang, unit)
        }

        locationUtils.requestLocation()
        Log.i("HomeFragment", "onCreate: done")
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentHour = LocalTime.now().hour
        val shared: PreferenceManager? = null

        if (currentHour >= 6 && currentHour < 18) {
            binding.root.setBackgroundResource(R.drawable.morning)
        } else {
            binding.root.setBackgroundResource(R.drawable.night)
        }
        cityTextView = view.findViewById(R.id.city)
        tempTextView = view.findViewById(R.id.temp)
        currentTextView = view.findViewById(R.id.condition)
        pressureText = view.findViewById(R.id.pressure)
        humidity = view.findViewById(R.id.clouds)
        windText = view.findViewById(R.id.wind)

        hourlyAdapter = HourlyAdapter(ArrayList(), requireActivity())
        val layoutManagerDaily = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.dailyrecyclerview.layoutManager = layoutManagerDaily
        binding.dailyrecyclerview.adapter = hourlyAdapter

        dayAdapter = DayAdapter(ArrayList(), requireActivity())
        val layoutManagerDay = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.eachdyrecycler.layoutManager = layoutManagerDay
        binding.eachdyrecycler.adapter = dayAdapter
        val lang = shared?.getSelectedLanguage(requireContext())
        val unit = shared?.getSelectedTemperatureUnit(requireContext())

        if (isNetworkAvailable()) {
            viewModel.getDayWeather(long, lat, lang, unit)
        } else {
            loadLastStoredResponse()
        }

        observeWeatherForecast()
    }

    private fun isNetworkAvailable(): Boolean {
        return false
    }

    private fun loadLastStoredResponse() {
        try {
            val context = requireContext().applicationContext
            val filename = "weather_data.json"

            val inputStream = context.openFileInput(filename)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            lastStoredResponse = Gson().fromJson(jsonString, WeatherResponse::class.java)
            lastStoredResponse?.let { updateUI(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading last stored response from file: ${e.message}", e)
        }
    }


    private fun observeWeatherForecast() {
        lifecycleScope.launch() {
            viewModel.forcast.collectLatest {
                when (it) {
                    is Utility.Success -> (updateUI(it.data))
                    else -> {}
                }
                Log.i(TAG, "observeWeatherForecast: ${it}")
            }
        }
    }

    private fun updateUI(response: WeatherResponse) {
        try {
            val context = requireContext().applicationContext
            val filename = "weather_data.json"

            saveResponseToFile(context, filename, response)

            val location = Location("").apply {
                latitude = response.lat
                longitude = response.lon
            }

            locationUtils.getAddress(location) { address ->
                val fullAddress = address ?: ""
                val city = response.address ?: ""
                val country = response.address ?: ""

                val finalAddress = if (fullAddress.isNotEmpty()) {
                    "$fullAddress, $city, $country"
                } else {
                    "$city, $country"
                }

                requireActivity().runOnUiThread {
                    try {
                        cityTextView.text = finalAddress
                        tempTextView.text = "${response.current.temp} "
                        currentTextView.text = "${response.current.clouds.toString()}"
                        humidity.text = "${response.current.humidity.toString()}"
                        pressureText.text = "${response.current.pressure}"

                        hourlyAdapter.setDay(response.hourly)
                        hourlyAdapter.notifyDataSetChanged()

                        dayAdapter.setWeekDay(response.daily)
                        dayAdapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating UI: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating UI: ${e.message}")
        }
    }

    private fun saveResponseToFile(context: Context, filename: String, response: WeatherResponse) {
        try {
            context.deleteFile(filename)
            val jsonString = Gson().toJson(response)
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving response to file: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
