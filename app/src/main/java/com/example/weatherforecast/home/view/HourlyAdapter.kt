package com.example.weatherforecast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.HourlyItemBinding
import com.example.weatherforecast.model.dto.HourlyItem
import com.example.weatherforecast.utilities.Formatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HourlyAdapter(private val context: Context): ListAdapter<HourlyItem, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtil()) {
    class HourlyViewHolder(val hourlyItemBinding: HourlyItemBinding) : RecyclerView.ViewHolder(hourlyItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val hourlyItemBinding = HourlyItemBinding.inflate(inflater, parent,false)
        return HourlyViewHolder(hourlyItemBinding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        var current = getItem(position)

        holder.hourlyItemBinding.textHour.text = Formatter.getFormattedHour(current.dt?.toLong()) ?: "00:00"

        holder.hourlyItemBinding.textTemperature.text = "${current.temp}°C"

        val image = "https://openweathermap.org/img/wn/${current.weather?.get(0)?.icon}@2x.png"
        Glide
            .with(context)
            .load(image)
            .centerCrop()
            .placeholder(R.drawable.hum_icon)
            .into(holder.hourlyItemBinding.tempImage)
    }
}


class HourlyDiffUtil : DiffUtil.ItemCallback<HourlyItem>(){
    override fun areItemsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
        return oldItem == newItem
    }

}
