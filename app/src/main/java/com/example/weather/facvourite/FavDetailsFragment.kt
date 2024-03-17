package com.example.weather.facvourite

import android.location.Geocoder
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
import com.example.weather.Home.DayAdapter
import com.example.weather.Home.HourlyAdapter
import com.example.weather.Model.WeatherModelFactory
import com.example.weather.Model.WeatherViewModel
import com.example.weather.Pojo.RepoImp
import com.example.weather.Pojo.WeatherResponse
import com.example.weather.R
import com.example.weather.Utility.Utility
import com.example.weather.databinding.FragmentFavDetailsBinding
import com.example.weather.network.RemoteDataSourceImp
import com.example.weather.settings.PreferenceManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Locale

class FavDetailsFragment : Fragment() {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: FragmentFavDetailsBinding
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dayAdapter: DayAdapter
    private lateinit var humedity: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentHour = LocalTime.now().hour

        if (currentHour >= 6 && currentHour < 18) {
            binding.root.setBackgroundResource(R.drawable.morning)
        } else {
            binding.root.setBackgroundResource(R.drawable.night)
        }

        val args = FavDetailsFragmentArgs.fromBundle(requireArguments())
        val latitude = args.fav!!.latitude
        val longitude = args.fav!!.longitude
        val localDataSource = LocalDataSourceImp(requireContext())
        val remoteDataSource = RemoteDataSourceImp()
        val modelFactory by lazy {
            WeatherModelFactory(
                RepoImp.getInstance(remoteDataSource, localDataSource),
                requireContext()
            )
        }

        viewModel = ViewModelProvider(this, modelFactory).get(WeatherViewModel::class.java)
        humedity = view.findViewById(R.id.clouds)

        initUI()
        observeWeatherForecast()
        reverseGeocode(latitude, longitude)

        if (isNetworkAvailable()) {
            viewModel.getDayWeather(latitude, longitude, null, null)
        } else {
            loadLastStoredResponse()
        }
    }

    private fun initUI() {
        binding.apply {
            hourlyAdapter = HourlyAdapter(ArrayList(), requireActivity())
            dailyrecyclerview.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
            dailyrecyclerview.adapter = hourlyAdapter

            dayAdapter = DayAdapter(ArrayList(), requireActivity())
            eachdyrecycler.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            eachdyrecycler.adapter = dayAdapter
        }
    }

    private fun reverseGeocode(latitude: Double, longitude: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: MutableList<android.location.Address>? =
                geocoder.getFromLocation(latitude, longitude, 1)
            val city = addresses?.firstOrNull()?.locality ?: "Unknown City"
            val country = addresses?.firstOrNull()?.countryName ?: "Unknown Country"
            val addressText = "$city, $country"

            binding.city.text = addressText
        }
    }

    private fun observeWeatherForecast() {
        lifecycleScope.launch {
            viewModel.forcast.collectLatest { weatherResult ->
                when (weatherResult) {
                    is Utility.Success -> updateUI(weatherResult.data)
                    else -> {
                    }
                }
            }
        }
    }

    private fun loadLastStoredResponse() {
        try {
            val context = requireContext().applicationContext
            val filename = "weather_data.json"

            val inputStream = context.openFileInput(filename)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val lastStoredResponse = Gson().fromJson(jsonString, WeatherResponse::class.java)
            lastStoredResponse?.let { updateUI(it) }
        } catch (e: Exception) {
            Log.e("FavDetailsFragment", "Error loading last stored response from file: ${e.message}", e)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        return false
    }

    private fun updateUI(response: WeatherResponse) {
        binding.apply {
            temp.text = "${response.current.temp} "
            humedity.text = "${response.current.humidity.toString()}"
            condition.text = "${response.current.clouds.toString()}"
            wind.text = "${response.current.wind_speed}"
            pressure.text = "${response.current.pressure}"

            hourlyAdapter.setDay(response.hourly)
            hourlyAdapter.notifyDataSetChanged()

            dayAdapter.setWeekDay(response.daily)
            dayAdapter.notifyDataSetChanged()
        }
    }
}
