package com.example.weatherforecast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.home.view_model.HomeViewModel
import com.example.weatherforecast.home.view_model.HomeViewModelFactory
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.utilities.ApiState
import com.example.weatherforecast.utilities.Formatter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


const val PERMISSION_ID = 3012

class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter

    private lateinit var layoutManagerDaily: LinearLayoutManager
    private lateinit var layoutManagerHourly: LinearLayoutManager

    private var isTakeLocation : Boolean = false

    lateinit var fusedClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return homeBinding.root
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance()//,
               // WeatherLocalDataSource.getInstance(requireContext())
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        dailyAdapter = DailyAdapter()
        hourlyAdapter = HourlyAdapter()

        layoutManagerDaily = LinearLayoutManager(context)
        homeBinding.recyclerDailyWeather.layoutManager = layoutManagerDaily
        layoutManagerHourly = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        homeBinding.recyclerHourlyWeather.layoutManager = layoutManagerHourly

        homeBinding.recyclerDailyWeather.adapter = dailyAdapter
        homeBinding.recyclerHourlyWeather.adapter = hourlyAdapter

        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherStateFlow.collectLatest {
                when(it){
                    is ApiState.Success -> {
                        Log.i("TAG", "onViewCreated: "+ it.weatherResponse.timezone)
                        homeBinding.currentData.text = Formatter.getCurrentDataAndTime()
                        homeBinding.desTemp.text = it.weatherResponse.current?.weather?.get(0)?.description ?: "UnKnow"
                        homeBinding.humitiyValue.text = (it.weatherResponse.current?.humidity ?: "0").toString()
                        homeBinding.textView2.text = (it.weatherResponse.current?.windSpeed  ?: "0").toString()
                        homeBinding.pressureValue.text = (it.weatherResponse.current?.pressure  ?: "0").toString()
                        homeBinding.cloudValue.text = (it.weatherResponse.current?.clouds  ?: "0").toString()
                        homeBinding.tempValue.text = "%.0f".format(it.weatherResponse.current?.temp) ?: "0"
                        hourlyAdapter.submitList(it.weatherResponse.hourly)
                        dailyAdapter.submitList(it.weatherResponse.daily)
                    }
                    is ApiState.Failed ->{
                        Log.i("TAG", "onViewCreated: failed" + it.msg.toString())

                    }
                    is ApiState.Loading ->{
                        Log.i("TAG", "onViewCreated: loading")
                    }
                    else -> {
                        Log.i("TAG", "onViewCreated: else")
                    }
                }
            }
        }

    }

    private fun isLocationEnabled(): Boolean {

        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(requireContext())
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list?.get(0)?.countryName + ", "+ list?.get(0)?.adminArea ?: "UnKnown"
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() :Unit{
        if(checkPermissions()){
            if(isLocationEnabled()) {
                requestNewLocationData()
            }else{
                Log.i("TAG" ,"Turn On Location")
                val intent =  Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(requireContext(),

            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        return result
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }else{

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(0)
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()
        )
    }

    private val locationCallBack : LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {

            if (!isTakeLocation) {
                isTakeLocation = true
                val lastLocation: Location? = p0.lastLocation
                currentLong = lastLocation?.longitude.toString()
                currentLat = lastLocation?.latitude.toString()

                homeBinding.countryName.text =
                    getAddress(currentLat.toDouble(), currentLong.toDouble())

                homeViewModel.getCurrentWeather(
                    currentLat.toDouble(),
                    currentLong.toDouble(),
                    "en",
                    "metric"
                )

            }
        }

    }

}