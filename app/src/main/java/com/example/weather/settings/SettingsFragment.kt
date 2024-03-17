package com.example.weather.settings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.DB.LocalDataSourceImp
import com.example.weather.Model.WeatherModelFactory
import com.example.weather.Model.WeatherViewModel
import com.example.weather.Pojo.RepoImp
import com.example.weather.R
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.network.RemoteDataSourceImp
import com.example.weather.settings.PreferenceManager
import java.time.LocalTime

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var localDataSource = LocalDataSourceImp(requireContext())
        val remoteDataSource = RemoteDataSourceImp()
        val modelFactory by lazy { WeatherModelFactory(RepoImp.getInstance(remoteDataSource,localDataSource),requireContext()) }

        viewModel = ViewModelProvider(this,modelFactory).get(WeatherViewModel::class.java)

        val currentHour = LocalTime.now().hour

        if (currentHour >= 6 && currentHour < 18) {
            binding.root.setBackgroundResource(R.drawable.morning)
        } else {
            binding.root.setBackgroundResource(R.drawable.night)
        }

        binding.arabic.setOnClickListener {
           PreferenceManager.saveSelectedLanguage(requireContext(), Constants.LANGUAGE_AR)
        }

        binding.english.setOnClickListener {
            PreferenceManager.saveSelectedLanguage(requireContext(), Constants.LANGUAGE_EN)
        }
        binding.celsius.setOnClickListener {
            PreferenceManager.saveSelectedTemperatureUnit(requireContext(), Constants.UNITS_CELSIUS)
        }

        binding.kelvin.setOnClickListener {
            PreferenceManager.saveSelectedTemperatureUnit(requireContext(), Constants.UNITS_kelvin)

        }
        binding.fahrehite.setOnClickListener {
            PreferenceManager.saveSelectedTemperatureUnit(requireContext(), Constants.UNITS_Fahrenheit)
        }

        val selectedUnit = PreferenceManager.getSelectedTemperatureUnit(requireContext())
        when (selectedUnit) {
            Constants.UNITS_kelvin -> binding.kelvin.isChecked = true
            Constants.UNITS_CELSIUS -> binding.celsius.isChecked = true
            Constants.UNITS_Fahrenheit -> binding.fahrehite.isChecked = true
        }
    }
}
