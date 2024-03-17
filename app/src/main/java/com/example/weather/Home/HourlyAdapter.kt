package com.example.weather.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.Pojo.Hourly
import com.example.weather.databinding.DayBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HourlyAdapter(private var hour:List<Hourly>, val context: Context) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {
    private lateinit var binding:DayBinding


    fun setDay(values:List<Hourly>){
        this.hour = values as List<Hourly>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyAdapter.ViewHolder {
      val inflater:LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        binding = DayBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =hour.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHour = hour[position]
        Glide
            .with(context)
            .load("https://openweathermap.org/img/wn/"+currentHour.weather.get(0).icon+".png")
            .into(binding.imageView2)
        holder.binding.hourCondition.text = currentHour.weather[0].description
       // val tempCelsius = TempUnits.convertKelvinToCelsius(currentHour.temp) ?:0.0
        holder.binding.hourTemp.text = "${currentHour.temp} "
        val currentTime = Date(currentHour.dt * 1000)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(currentTime)
        holder.binding.time.text = formattedTime
    }





    inner class ViewHolder(var binding: DayBinding): RecyclerView.ViewHolder(binding.root)
}