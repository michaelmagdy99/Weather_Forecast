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
import java.util.Calendar
import java.util.Locale

class DailyAdapter:ListAdapter<DailyItem, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    lateinit var dailyItemBinding: DailyItemBinding
    class DailyViewHolder(val dailyItemBinding: DailyItemBinding) :RecyclerView.ViewHolder(dailyItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        dailyItemBinding = DailyItemBinding.inflate(inflater, parent,false)
        return DailyViewHolder(dailyItemBinding)    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        var current = getItem(position)

//        holder.dailyItemBinding.textDay.text = getDayOfWeek(current.dt?.toLong())

        holder.dailyItemBinding.imageWeather.setImageResource(R.drawable.sunny)

      //  holder.dailyItemBinding.textDescription.text = current.weather?.get(0)?.description

       // holder.dailyItemBinding.textTemperature.text = "${current.temp?.day}°C"

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
    if (timestamp == null) return ""

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000

    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
    return dayOfWeek ?: ""
}