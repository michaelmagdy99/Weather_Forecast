package com.example.weatherforecast.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.favourite.view_model.FavouriteViewModel
import com.example.weatherforecast.favourite.view_model.FavouriteViewModelFactory
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.LocationKey
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mapBinding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap

    private lateinit var favViewModel: FavouriteViewModel
    private lateinit var favViewModelFactory: FavouriteViewModelFactory

    var latitude = 0.0
    var longitude = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapBinding = FragmentMapBinding.inflate(inflater, container, false)
        mapBinding.map.onCreate(savedInstanceState)
        mapBinding.cardLocation.visibility = View.GONE
        mapBinding.map.getMapAsync(this)

        MainActivity.isMapFragment = true

        favViewModelFactory = FavouriteViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource.getInstance(requireContext())))
        favViewModel = ViewModelProvider(this, favViewModelFactory).get(FavouriteViewModel::class.java)

        val args = MapFragmentArgs.fromBundle(requireArguments())
        val fromMap = args.from

        mapBinding.saveBtnLocation.setOnClickListener {
            if (fromMap == "map"){
                val action = MapFragmentDirections.actionMapToHome()
                action.setDestinationDescription("map")
                action.setFavLocation(FaviourateLocationDto(LocationKey(latitude,longitude)
                    ,getAddress(latitude,longitude),"0"))
                Navigation.findNavController(requireView()).navigate(action)
            }else if (fromMap == "fav"){
                favViewModel.insertLocation(FaviourateLocationDto(LocationKey(latitude,longitude)
                    ,getAddress(latitude,longitude),"0"))
                Navigation.findNavController(it).navigate(R.id.action_map_to_favourite)
            }
        }

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
        googleMap.setOnMapClickListener(this)
    }

    override fun onMapClick(latLng: LatLng) {
        mapBinding.cardLocation.visibility = View.VISIBLE
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(latLng).title("Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        latitude = latLng.latitude
        longitude = latLng.longitude

        val latitudeStr = String.format("%.6f", latitude)
        val longitudeStr = String.format("%.6f", longitude)

        mapBinding.locationName.text = getAddress(latitude,longitude)
        mapBinding.locationLat.text = "$latitudeStr ,  $longitudeStr"

        Log.i("TAG", "onMapClick: $latitudeStr $longitudeStr")

    }

    private fun getAddress(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext())
        val list = geocoder.getFromLocation(lat, lon, 1)
        return list?.get(0)?.countryName + ", "+ list?.get(0)?.adminArea ?: "UnKnown"
    }
}
