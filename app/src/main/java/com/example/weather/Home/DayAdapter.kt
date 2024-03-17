package com.example.weather.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.Pojo.Daily
import com.example.weather.Pojo.WeatherResponse
import com.example.weather.databinding.EachDayBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayAdapter(private var day:List<Daily>, val context: Context) :
    RecyclerView.Adapter<DayAdapter.ViewHolder>() {
    private lateinit var binding:EachDayBinding
    lateinit var response:WeatherResponse


    fun setWeekDay(values:List<Daily>){
        this.day = values as List<Daily>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayAdapter.ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        binding = EachDayBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =day.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDay = day[position]
        Glide
            .with(context)
            .load("https://openweathermap.org/img/wn/"+currentDay.weather.get(0).icon+".png")
            .into(binding.imageView)
        holder.binding.descdy.text = currentDay.weather[0].description
        //val tempCelsius = TempUnits.convertKelvinToCelsius(currentDay.temp?.max)
        holder.binding.dytemp.text = "${currentDay.temp?.max} °C"

        val currentTime = Date(currentDay.dt * 1000)

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val formattedDay = dayFormat.format(currentTime)
        holder.binding.Day.text = formattedDay

    }

//    // Function to map weather icon name to corresponding drawable resource ID
//    private fun getWeatherIconResId(iconName: String): Int {
//        return when (iconName) {
//            "01d" -> R.drawable.clearsky_sun
//            "02d" -> R.drawable.few_clouds_moon
//            "03d" -> R.drawable.broken_clouds
//            "04d" -> R.drawable.rain
//            else -> R.drawable.img
//        }
//    }


    inner class ViewHolder(var binding: EachDayBinding): RecyclerView.ViewHolder(binding.root)
}
