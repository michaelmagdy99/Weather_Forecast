package com.example.weatherforecast.home.view

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.weatherforecast.utilities.ApiState
import com.example.weatherforecast.utilities.Formatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


const val PERMISSION_ID = 3012

class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter

    private lateinit var layoutManagerDaily: LinearLayoutManager
    private lateinit var layoutManagerHourly: LinearLayoutManager


    private var isFavourite = false

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
            isFavourite = true
            homeViewModel.getFavoriteWeather(favLocation.locationKey.lat,favLocation.locationKey.long)
        }else{
            isFavourite = false
            homeViewModel.getCurrentWeather()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherStateFlow.collectLatest {
                when(it){
                    is ApiState.Success -> {
                        Log.i("TAG", "onViewCreated: "+ it.data.timezone)
                        setWeatherDataToViews(it.data)
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


    private fun setWeatherDataToViews(weatherResponse: WeatherResponse) {
        val description =  weatherResponse.current?.weather?.get(0)?.description ?: "UnKnow"
        val temp = weatherResponse.current?.temp?.toInt() ?: 0

        val imageUrl = "https://openweathermap.org/img/wn/${weatherResponse.current?.weather?.get(0)?.icon}@2x.png"
        Glide
            .with(requireContext())
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.hum_icon)
            .into(homeBinding.tempImageDes)

        homeBinding.currentData.text = Formatter.getCurrentDataAndTimeFromUnix(weatherResponse.current?.dt)
        homeBinding.countryName.text = getAddress(weatherResponse.lat!!.toDouble(), weatherResponse.lon!!.toDouble())
        homeBinding.desTemp.text =description
        homeBinding.humitiyValue.text = (weatherResponse.current?.humidity ?: "0").toString()
        homeBinding.textView2.text = (weatherResponse.current?.windSpeed  ?: "0").toString()
        homeBinding.pressureValue.text = (weatherResponse.current?.pressure  ?: "0").toString()
        homeBinding.cloudValue.text = (weatherResponse.current?.clouds  ?: "0").toString()
        homeBinding.tempValue.text = temp.toString()
        hourlyAdapter.submitList(weatherResponse.hourly)
        dailyAdapter.submitList(weatherResponse.daily)

        lifecycleScope.launch {
            homeViewModel.addressStateFlow.collect { address ->
                homeBinding.countryName.text = address
            }
        }

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

    fun getAddress(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext())
        val list = geocoder.getFromLocation(lat, lon, 1)
        return list?.get(0)?.countryName + ", "+ list?.get(0)?.adminArea ?: "UnKnown"
    }

}