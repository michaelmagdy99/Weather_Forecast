package com.example.weatherforecast.home.view

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
import com.example.weatherforecast.utilities.Converts
import com.example.weatherforecast.utilities.Formatter
import com.example.weatherforecast.utilities.LanguageUtilts
import com.example.weatherforecast.utilities.NetworkConnection
import com.example.weatherforecast.utilities.SettingsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        LanguageUtilts.setAppLocale(SettingsConstants.getLang(),requireContext())
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
//            homeBinding.countryName.text = GetLocation.getAddress(requireContext(),true)
        }else if (args.destinationDescription == "current"){
            homeViewModel.getCurrentWeather()
        }else if (args.destinationDescription == "map"){
            homeViewModel.getFavoriteWeather(favLocation?.locationKey?.lat ?: 0.0,favLocation?.locationKey?.long ?:0.0)
        }else if (args.destinationDescription == "alert"){

        }


        homeBinding.swipeContainer.setOnRefreshListener {
            if(mode==0) {
                if (NetworkConnection.checkConnectionState(requireActivity()))
//                    homeViewModel.getAllFromNetwork(
//                        requireContext(),
//                        true,
//                        isFavorite = false
//                    )
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

        val imageUrl = "https://openweathermap.org/img/wn/${weatherResponse.current?.weather?.get(0)?.icon}@2x.png"
        Glide
            .with(requireContext())
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.hum_icon)
            .into(homeBinding.tempImageDes)

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

}