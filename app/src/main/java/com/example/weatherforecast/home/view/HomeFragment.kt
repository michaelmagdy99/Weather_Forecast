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
                WeatherLocalDataSource.getInstance(requireContext())
            ),requireContext()
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
        val dateTimeWithPeriod = Formatter.getCurrentPeriod()
        val temp = weatherResponse.current?.temp?.toInt() ?: 0
        homeBinding.currentData.text = Formatter.getCurrentDataAndTime()
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
        val suitableBackground = Formatter.getSuitableBackground(requireContext(),
            description,
            dateTimeWithPeriod,
            homeBinding.weatherView)

        suitableBackground?.let {
            homeBinding.background.background = it

        }
    }


}