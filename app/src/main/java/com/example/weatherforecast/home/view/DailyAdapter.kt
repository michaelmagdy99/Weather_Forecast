package com.example.weatherforecast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.DailyItemBinding
import com.example.weatherforecast.model.dto.DailyItem
import com.example.weatherforecast.utilities.Formatter

class DailyAdapter(val context: Context):ListAdapter<DailyItem, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    class DailyViewHolder(val dailyItemBinding: DailyItemBinding) :RecyclerView.ViewHolder(dailyItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val dailyItemBinding = DailyItemBinding.inflate(inflater, parent,false)
        return DailyViewHolder(dailyItemBinding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        var current = getItem(position)


        holder.dailyItemBinding.textDay.text = Formatter.getDay(current.dt)

        holder.dailyItemBinding.imageWeather.setImageResource(Formatter.getWeatherImage(current.weather?.get(0)?.icon ?: "01d"))
        holder.dailyItemBinding.textDescription.text = current.weather?.get(0)?.description

        holder.dailyItemBinding.textTemperature.text = "${current.temp?.min} / ${current.temp?.max}°C"

    }
}


class DailyDiffUtil : DiffUtil.ItemCallback<DailyItem>(){
    override fun areItemsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem == newItem
    }

}
