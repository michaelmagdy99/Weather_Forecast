package com.example.weatherforecast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.HourlyItemBinding
import com.example.weatherforecast.model.dto.HourlyItem

class HourlyAdapter: ListAdapter<HourlyItem, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtil()) {
    lateinit var houlyItemBinding: HourlyItemBinding
    class HourlyViewHolder(val houlyItemBinding: HourlyItemBinding) : RecyclerView.ViewHolder(houlyItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        houlyItemBinding = HourlyItemBinding.inflate(inflater, parent,false)
        return HourlyViewHolder(houlyItemBinding)    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        var current = getItem(position)

      //  holder.houlyItemBinding.textHour.text = getFormattedHour(current.dt)

        holder.houlyItemBinding.tempImage.setImageResource(R.drawable.sunny)

       // holder.houlyItemBinding.textTemperature.text = "${current.temp}°C"
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

private fun getFormattedHour(hour: Int?): String {
    if (hour == null) return ""

    val formattedHour = if (hour == 12 || hour == 0) {
        "12"
    } else {
        (hour % 12).toString()
    }

    val amPm = if (hour < 12) "AM" else "PM"

    return "$formattedHour:00 $amPm"
}