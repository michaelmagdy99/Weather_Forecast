package com.example.weatherforecast.home.view

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.home.view_model.HomeViewModel
import com.example.weatherforecast.home.view_model.HomeViewModelFactory
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import com.example.weatherforecast.utilities.UiState
import com.example.weatherforecast.utilities.Converts
import com.example.weatherforecast.utilities.Formatter
import com.example.weatherforecast.utilities.LocationUtils
import com.example.weatherforecast.utilities.NetworkConnection
import com.example.weatherforecast.utilities.SettingsConstants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException


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
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource.getInstance(requireContext())
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
            val lat = SharedPreferencesHelper.getInstance(requireActivity()).loadCurrentLocation("lat")?.toDouble() ?: 29.3059751
            val long = SharedPreferencesHelper.getInstance(requireActivity()).loadCurrentLocation("long")?.toDouble() ?: 30.8549351
            homeViewModel.getCurrentWeather(lat, long)
        }

        //check network and load last data in file
        if (!NetworkConnection.checkConnectionState(requireActivity())) {
            setWeatherDataToViews(loadWeatherResponseFromFile()!!)
            Snackbar.make(view, getString(R.string.network_connection_has_been_issues), Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss") { }
                .show()
        }
        else {
            lifecycleScope.launch(Dispatchers.Main) {
                homeViewModel.weatherStateFlow.collectLatest {
                    when (it) {
                        is UiState.Success -> {
                            saveResponseToFile(it.data)
                            Log.i("TAG", "onViewCreated: " + it.data.timezone)
                            setWeatherDataToViews(it.data)
                            homeBinding.progressBar.visibility = View.GONE
                            homeBinding.background.visibility = View.VISIBLE
                            homeBinding.emptyData.visibility = View.GONE
                            homeBinding.textDataNo.visibility = View.GONE
                            homeBinding.home.visibility = View.GONE
                        }

                        is UiState.Failed -> {
                            Log.i("TAG", "onViewCreated: failed" + it.msg.toString())
                            homeBinding.progressBar.visibility = View.GONE
                            homeBinding.background.visibility = View.GONE
                            homeBinding.emptyData.visibility = View.VISIBLE
                            homeBinding.textDataNo.visibility = View.VISIBLE
                            homeBinding.home.visibility = View.VISIBLE
                        }

                        is UiState.Loading -> {
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
    }


    private fun setWeatherDataToViews(weatherResponse: WeatherResponse) {
        val description =  weatherResponse.current?.weather?.get(0)?.icon ?: "UnKnow"
        val temp = weatherResponse.current?.temp?.toInt() ?: 0

        homeBinding.tempImageDes.setImageResource(Formatter.getWeatherImage(weatherResponse.current?.weather?.get(0)?.icon ?: "01d"))
        val location = Location("").apply {
            latitude = weatherResponse.lat ?: 0.0
            longitude = weatherResponse.lon ?: 0.0
        }
        homeBinding.countryName.text = LocationUtils.getAddress(requireActivity(), location)
        homeBinding.currentData.text = Formatter.getDate(weatherResponse.current?.dt) + " | " +  (Formatter.getFormattedHour(weatherResponse.current?.dt?.toLong())) ?: "12:00 PM"
        homeBinding.desTemp.text = weatherResponse.current?.weather?.get(0)?.description ?: "UnKnow"
        homeBinding.humitiyValue.text = (weatherResponse.current?.humidity ?: "0").toString() + " %"
        homeBinding.textView2.text = (Converts.getWindSpeed(weatherResponse.current?.windSpeed) ?: "0").toString() + " "+ SettingsConstants.getWindSpeed()
        homeBinding.pressureValue.text = (weatherResponse.current?.pressure  ?: "0").toString()
        homeBinding.cloudValue.text = (weatherResponse.current?.clouds  ?: "0").toString()
        homeBinding.tempValue.text = Converts.getTemperature(temp).toString()
        homeBinding.tempMeasure.text = SettingsConstants.getTemp().toString()
        hourlyAdapter.submitList(weatherResponse.hourly)
        dailyAdapter.submitList(weatherResponse.daily)


        val suitableBackground = Formatter.getSuitableBackground(
            requireContext(),
            description,
            homeBinding.weatherView
        )

        suitableBackground?.let {
            homeBinding.background.background = it

        }
    }

    private fun saveResponseToFile(response: WeatherResponse) {
        val context = requireContext().applicationContext
        val filename = "weather_response.json"
        val jsonString = Gson().toJson(response)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
            outputStream.write(jsonString.toByteArray())
        }
    }


    private fun loadWeatherResponseFromFile(): WeatherResponse? {
        val filename = "weather_response.json"
        return try {
            val inputStream = requireActivity().openFileInput(filename)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, WeatherResponse::class.java)
        } catch (e: FileNotFoundException) {
            null
        } catch (e: IOException) {
            null
        }
    }

}