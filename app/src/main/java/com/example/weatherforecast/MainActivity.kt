package com.example.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.home.view.PERMISSION_ID
import com.example.weatherforecast.utilities.LanguageUtils
import com.example.weatherforecast.utilities.SettingsConstants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var homeActivityMainBinding: ActivityMainBinding

    lateinit var sharedPreferences: SharedPreferencesHelper

    private var isTakeLocation : Boolean = false

    lateinit var fusedClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    companion object {
          var isMapFragment: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeActivityMainBinding.root)

        sharedPreferences = SharedPreferencesHelper.getInstance(this)
        sharedPreferences.loadData()

        LanguageUtils.setAppLocale(SettingsConstants.getLang(), this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph, homeActivityMainBinding.drawerLayout)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val destinationId = if (!sharedPreferences.isDialogShown()) {
            R.id.chooseDialogFragment
        } else {
            SettingsConstants.getLocation()
        }

        navController.navigate(destinationId)

        NavigationUI.setupWithNavController(
            homeActivityMainBinding.navigationView,
            navController
        )

        homeActivityMainBinding.toolbarButton.setOnClickListener {
            if (isMapFragment) {
                navController.popBackStack()
                isMapFragment = false
            } else {
                homeActivityMainBinding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            isMapFragment = destination.id == R.id.map
            if (isMapFragment) {
                homeActivityMainBinding.toolbarButton.setImageResource(R.drawable.back)
            } else {
                homeActivityMainBinding.toolbarButton.setImageResource(R.drawable.menu)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        getLocation()
        val isNewSettingsRestart = sharedPreferences.isNewSettingsRestart()
        if (isNewSettingsRestart == 1) {
            sharedPreferences.loadData()
            val destinationId = SettingsConstants.getLocation()
            navController.navigate(destinationId)
            sharedPreferences.saveAsNewSetting(0)
        }
        else {
            sharedPreferences.saveAsNewSetting(0)
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
        val result = ActivityCompat.checkSelfPermission(this,

            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        return result
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager =  getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(0)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

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
                sharedPreferences.let {
                    it.saveCurrentLocation("lat", lastLocation?.latitude)
                    it.saveCurrentLocation("long", lastLocation?.longitude)
                }
            }
        }
    }
}

