package com.example.weather.facvourite

import FavAdapter
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.DB.LocalDataSourceImp
import com.example.weather.Model.WeatherModelFactory
import com.example.weather.Model.WeatherViewModel
import com.example.weather.Pojo.FavoriteLocation
import com.example.weather.Pojo.RepoImp
import com.example.weather.R
import com.example.weather.Utility.Utility
import com.example.weather.databinding.FragmentFavouriteBinding
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.network.RemoteDataSourceImp
import kotlinx.coroutines.launch
import java.time.LocalTime


class FavouriteFragment : Fragment(),OnClick {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var recyclerView: RecyclerView
    lateinit var favAdapter: FavAdapter
    lateinit var binding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

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
        setupRecyclerView()
        initializeViewModel()

    }


    private fun setupRecyclerView() {
        recyclerView = binding.favRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favAdapter = FavAdapter(emptyList(), requireContext(),this)
        recyclerView.adapter = favAdapter
    }

    private fun initializeViewModel() {
        val localDataSource = LocalDataSourceImp(requireContext())
        val remoteDataSource = RemoteDataSourceImp()
        val modelFactory by lazy { WeatherModelFactory(RepoImp.getInstance(remoteDataSource, localDataSource),requireContext()) }
        viewModel = ViewModelProvider(this, modelFactory).get(WeatherViewModel::class.java)
        viewModel.getAllFromDB()
        lifecycleScope.launch {
            viewModel.favourite.collect { utility ->
                when (utility) {
                    is Utility.Successful -> favAdapter.updateData(utility.fav)
                    is Utility.Failure -> Log.e(ContentValues.TAG, "Error: ${utility.msg.message}")
                    Utility.Loading -> Log.d(ContentValues.TAG, "Loading")
                    else -> {}
                }
            }
        }
    }

    override fun onClick(favoriteLocation: FavoriteLocation) {
        viewModel.deletFromDB(favoriteLocation)
    }
}
