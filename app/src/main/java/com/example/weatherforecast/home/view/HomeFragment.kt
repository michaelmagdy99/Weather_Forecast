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
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.home.view_model.HomeViewModel
import com.example.weatherforecast.home.view_model.HomeViewModelFactory
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import com.example.weatherforecast.utilities.ApiState
import com.example.weatherforecast.utilities.Converts
import com.example.weatherforecast.utilities.Formatter
import com.example.weatherforecast.utilities.LocationUtils
import com.example.weatherforecast.utilities.NetworkConnection
import com.example.weatherforecast.utilities.SettingsConstants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log


const val PERMISSION_ID = 3012

class HomeFragment : Fragment() {
    val mode = 0
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource.getInstance(requireContext()),requireContext()
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        dailyAdapter = DailyAdapter(requireContext())
        hourlyAdapter = HourlyAdapter(requireContext())

        layoutManagerDaily = LinearLayoutManager(context)
        homeBinding.recyclerDailyWeather.layoutManager = layoutManagerDaily
        layoutManagerHourly = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        homeBinding.recyclerHourlyWeather.layoutManager = layoutManagerHourly

        homeBinding.recyclerDailyWeather.adapter = dailyAdapter
        homeBinding.recyclerHourlyWeather.adapter = hourlyAdapter

        val args = HomeFragmentArgs.fromBundle(requireArguments())
        val favLocation = args.favLocation

        if (favLocation!=null){
            homeViewModel.getFavoriteWeather(favLocation.locationKey.lat,favLocation.locationKey.long)
        }else if (args.destinationDescription == "map"){
            homeViewModel.getFavoriteWeather(favLocation?.locationKey?.lat ?: 0.0,favLocation?.locationKey?.long ?:0.0)
        }else if (args.destinationDescription == "alert"){
        }else{
            homeViewModel.getCurrentWeather()
            getLocation()
        }


        homeBinding.swipeContainer.setOnRefreshListener {
            if(mode==0) {
                if (NetworkConnection.checkConnectionState(requireActivity()))

                else {
                    homeViewModel.getCurrentWeather()
                }
            }else{
                homeBinding.swipeContainer.isRefreshing=false
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherStateFlow.collectLatest {
                when(it){
                    is ApiState.Success -> {
                        Log.i("TAG", "onViewCreated: "+ it.data.timezone)
                        setWeatherDataToViews(it.data)
                        homeBinding.progressBar.visibility = View.GONE
                        homeBinding.background.visibility = View.VISIBLE
                        homeBinding.emptyData.visibility = View.GONE
                        homeBinding.textDataNo.visibility = View.GONE
                        homeBinding.home.visibility = View.GONE
                    }
                    is ApiState.Failed -> {
                        Log.i("TAG", "onViewCreated: failed" + it.msg.toString())
                        homeBinding.progressBar.visibility = View.GONE
                        homeBinding.background.visibility = View.GONE
                        homeBinding.emptyData.visibility = View.VISIBLE
                        homeBinding.textDataNo.visibility = View.VISIBLE
                        homeBinding.home.visibility = View.VISIBLE
                    }
                    is ApiState.Loading -> {
                        Log.i("TAG", "onViewCreated: loading")
                        homeBinding.background.visibility = View.GONE
                        homeBinding.progressBar.visibility = View.VISIBLE
                        homeBinding.emptyData.visibility = View.GONE
                        homeBinding.textDataNo.visibility = View.GONE
                        homeBinding.home.visibility = View.VISIBLE
                    }
                    else -> {
                        Log.i("TAG", "onViewCreated: else")
                    }
                }
            }
        }

    }


    private fun setWeatherDataToViews(weatherResponse: WeatherResponse) {
        val description =  weatherResponse.current?.weather?.get(0)?.description ?: "UnKnow"
        val temp = weatherResponse.current?.temp?.toInt() ?: 0

//        val imageUrl = "https://openweathermap.org/img/wn/${weatherResponse.current?.weather?.get(0)?.icon}@2x.png"
//        Glide
//            .with(requireContext())
//            .load(imageUrl)
//            .centerCrop()
//            .placeholder(R.drawable.hum_icon)
//            .into(homeBinding.tempImageDes)


        homeBinding.tempImageDes.setImageResource(Formatter.getWeatherImage(weatherResponse.current?.weather?.get(0)?.icon ?: "01d"))
        val location = Location("").apply {
            latitude = weatherResponse.lat ?: 0.0
            longitude = weatherResponse.lon ?: 0.0
        }
        homeBinding.countryName.text = LocationUtils.getAddress(requireActivity(), location)
        homeBinding.currentData.text = Formatter.getDate(weatherResponse.current?.dt)
        homeBinding.desTemp.text =description
        homeBinding.humitiyValue.text = (weatherResponse.current?.humidity ?: "0").toString() + " %"
        homeBinding.textView2.text = (Converts.getWindSpeed(weatherResponse.current?.windSpeed) ?: "0").toString() + " "+ SettingsConstants.getWindSpeed()
        homeBinding.pressureValue.text = (weatherResponse.current?.pressure  ?: "0").toString()
        homeBinding.cloudValue.text = (weatherResponse.current?.clouds  ?: "0").toString()
        homeBinding.tempValue.text = Converts.getTemperature(temp).toString()
        homeBinding.tempMeasure.text = SettingsConstants.getTemp().toString()
        hourlyAdapter.submitList(weatherResponse.hourly)
        dailyAdapter.submitList(weatherResponse.daily)



        val (sunriseTime, sunsetTime) = Formatter.getSunriseAndSunset(
            (weatherResponse.current?.sunrise ?: 0) * 1000L,
            (weatherResponse.current?.sunset ?: 0) * 1000L
        )

        val currentTimePeriod = if (System.currentTimeMillis().toString() in sunriseTime..sunsetTime) {
            "AM"
        } else {
            "PM"
        }

        val suitableBackground = Formatter.getSuitableBackground(
            requireContext(),
            description,
            currentTimePeriod,
            homeBinding.weatherView
        )

        suitableBackground?.let {
            homeBinding.background.background = it

        }
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
    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(requireActivity(),

            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        return result
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager =  context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Unit {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                Log.i("TAG", "Turn On Location")
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun requestPermissions(){
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
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
                SharedPreferencesHelper.getInstance(requireActivity()).let {
                    it.saveCurrentLocation("lat", lastLocation?.latitude)
                    it.saveCurrentLocation("long", lastLocation?.longitude)
                }
            }
        }
    }
}