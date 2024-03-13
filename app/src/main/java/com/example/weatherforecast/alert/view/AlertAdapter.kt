package com.example.weatherforecast.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.AlertItemBinding
import com.example.weatherforecast.databinding.FavItemBinding
import com.example.weatherforecast.favourite.view.FavouriteAdapter
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto

class AlertAdapter(val context: Context,
                   private val onClick : (Alert) -> Unit

): ListAdapter<Alert, AlertAdapter.AlertsViewHolder>(AlertsDiffUtil()) {
    class AlertsViewHolder(val alertItemBinding: AlertItemBinding ) : RecyclerView.ViewHolder(alertItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val alertItemBinding = AlertItemBinding.inflate(inflater, parent,false)
        return AlertsViewHolder(alertItemBinding)
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        var current = getItem(position)

        holder.alertItemBinding.textViewAlertTimeFrom.text = "From : " + current.from + " | " + current.to

        holder.alertItemBinding.imgBtnAlertItem.setOnClickListener {
            onClick(current)
        }

    }

    fun deleteItemAtPosition(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }
}



class AlertsDiffUtil : DiffUtil.ItemCallback<Alert>(){
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }

}