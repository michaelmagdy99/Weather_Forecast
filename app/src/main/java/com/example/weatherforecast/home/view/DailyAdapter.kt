package com.example.weatherforecast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.DailyItemBinding
import com.example.weatherforecast.model.dto.DailyItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyAdapter:ListAdapter<DailyItem, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    class DailyViewHolder(val dailyItemBinding: DailyItemBinding) :RecyclerView.ViewHolder(dailyItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val dailyItemBinding = DailyItemBinding.inflate(inflater, parent,false)
        return DailyViewHolder(dailyItemBinding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        var current = getItem(position)

        holder.dailyItemBinding.textDay.text = getDayOfWeek(current.dt?.toLong())

        holder.dailyItemBinding.imageWeather.setImageResource(R.drawable.sunny)

        holder.dailyItemBinding.textDescription.text = current.weather?.get(0)?.description

        holder.dailyItemBinding.textTemperature.text = "${current.temp?.day}°C"

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

private fun getDayOfWeek(timestamp: Long?): String {
    val date = Date(timestamp!! * 1000)
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    val dayOfWeek: String = sdf.format(date)
    return dayOfWeek ?: ""
}