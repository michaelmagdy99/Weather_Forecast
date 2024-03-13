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

class AlertAdapter(private val removeClickListener: RemoveClickListener

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

        holder.alertItemBinding.textViewAlertTimeFrom.text = "From : " + current.fromDate + " | " + current.fromTime
        holder.alertItemBinding.textViewAlertTimeTo.text = "To : " + current.toDate + " | " + current.toTime

        holder.alertItemBinding.imgBtnAlertItem.setOnClickListener {
            removeClickListener.onRemoveClick(current)
        }

    }


    class RemoveClickListener(val removeClickListener : (Alert) -> Unit){
        fun onRemoveClick(alertEntity: Alert) = removeClickListener(alertEntity)
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