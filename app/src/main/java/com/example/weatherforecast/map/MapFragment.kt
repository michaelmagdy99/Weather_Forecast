package com.example.weatherforecast.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.utilities.SharedPreferencesHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() , OnMapReadyCallback {

    lateinit var mapBinding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mapBinding = FragmentMapBinding.inflate(inflater, container, false)
        mapBinding.map.onCreate(savedInstanceState)
        mapBinding.map.getMapAsync(this)
        return mapBinding.root
    }

    override fun onResume() {
        super.onResume()
        mapBinding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapBinding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapBinding.map.onDestroy()
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val lat = SharedPreferencesHelper.getInstance(requireContext()).loadData("latitude")?.toDouble() ?: 0.0
        val lon = SharedPreferencesHelper.getInstance(requireContext()).loadData("longitude")?.toDouble() ?: 0.0
        val currentLocation = LatLng(lat, lon)
        googleMap.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
    }

}