package com.example.weatherforecast.favourite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.DailyItemBinding
import com.example.weatherforecast.databinding.FavItemBinding
import com.example.weatherforecast.home.view.DailyAdapter
import com.example.weatherforecast.model.dto.DailyItem
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.utilities.Formatter

class FavouriteAdapter(val context: Context,
                       private val onClick : (FaviourateLocationDto) -> Unit

): ListAdapter<FaviourateLocationDto, FavouriteAdapter.FavViewHolder>(FavDiffUtil()) {
    class FavViewHolder(val favItemBinding: FavItemBinding) : RecyclerView.ViewHolder(favItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val favItemBinding = FavItemBinding.inflate(inflater, parent,false)
        return FavViewHolder(favItemBinding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        var current = getItem(position)

        //val image = "https://openweathermap.org/img/wn/${current.weather?.get(0)?.icon}@2x.png"

        holder.favItemBinding.locationName.text = current.countryName.take(10)

        holder.favItemBinding.locationLat.text =
            "${String.format("%.2f", current.locationKey.lat)}, ${String.format("%.2f", current.locationKey.long)}"


        holder.favItemBinding.cardLocation.setOnClickListener {
            onClick(current)
        }
    }

    fun deleteItemAtPosition(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }
}



class FavDiffUtil : DiffUtil.ItemCallback<FaviourateLocationDto>(){
    override fun areItemsTheSame(oldItem: FaviourateLocationDto, newItem: FaviourateLocationDto): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: FaviourateLocationDto, newItem: FaviourateLocationDto): Boolean {
        return oldItem == newItem
    }

}