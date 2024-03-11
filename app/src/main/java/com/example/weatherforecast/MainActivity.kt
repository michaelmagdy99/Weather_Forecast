package com.example.weatherforecast

import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.utilities.LanguageUtilts
import com.example.weatherforecast.utilities.SettingsConstants

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var homeActivityMainBinding: ActivityMainBinding

    lateinit var sharedPreferences: SharedPreferencesHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeActivityMainBinding.root)

        sharedPreferences = SharedPreferencesHelper.getInstance(this)
        sharedPreferences.loadData()

        LanguageUtilts.setAppLocale(SettingsConstants.getLang(), this)

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
            homeActivityMainBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
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
}

