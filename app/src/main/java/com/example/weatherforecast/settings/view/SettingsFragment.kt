package com.example.weatherforecast.settings.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.utilities.LanguageUtilts
import com.example.weatherforecast.utilities.SettingsConstants
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper

class SettingsFragment : Fragment() {

    private lateinit var settingsBinding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        settingsBinding = FragmentSettingsBinding.inflate(inflater,container,false)
        return settingsBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSettingsUi()

        settingsBinding.saveBtn.setOnClickListener {
            saveSettingsData()
        }
    }

    private fun saveSettingsData() {
        when (settingsBinding.tempRadioGroup.checkedRadioButtonId) {
            R.id.radio_celsius -> {
                SettingsConstants.temperature = SettingsConstants.Temperature.C
            }
            R.id.radio_fahrenheit -> {
                SettingsConstants.temperature = SettingsConstants.Temperature.F
            }
            else -> {
                SettingsConstants.temperature = SettingsConstants.Temperature.K
            }
        }

        SettingsConstants.notificationState =
            if (settingsBinding.notifiactionRadioGroup.checkedRadioButtonId == R.id.radio_enable) {
                SettingsConstants.NotificationState.ON
            } else {
                SettingsConstants.NotificationState.OFF
            }

        SettingsConstants.windSpeed =
            if (settingsBinding.windRadioGroup.checkedRadioButtonId == R.id.radio_meter_sec) {
                SettingsConstants.WindSpeed.M_S
            } else {
                SettingsConstants.WindSpeed.MILE_HOUR
            }

        SettingsConstants.lang =
            if (settingsBinding.languageRadioGroup.checkedRadioButtonId == R.id.radio_arabic) {
                SettingsConstants.Lang.AR
            } else {
                SettingsConstants.Lang.EN
            }

        SettingsConstants.location =
            if (settingsBinding.locationRadioGroup.checkedRadioButtonId == R.id.radio_gps) {
                SettingsConstants.Location.GPS
            } else {
                SettingsConstants.Location.MAP
            }

        val pref = SharedPreferencesHelper(requireContext())
        pref.insertInData()
        pref.saveAsNewSetting(1)

        LanguageUtilts.setAppLocale(SettingsConstants.getLang(), requireContext())
        LanguageUtilts.setAppLayoutDirections(SettingsConstants.getLang(), requireContext())
        LanguageUtilts.changeLang(requireActivity(), SettingsConstants.getLang())

        requireActivity().recreate()
    }



    private fun initSettingsUi() {

        if(SettingsConstants.getLang()=="en") {
            settingsBinding.languageRadioGroup.check(R.id.radio_english)
        }else{
            settingsBinding.languageRadioGroup.check(R.id.radio_arabic)
        }

        if(SettingsConstants.location==SettingsConstants.Location.GPS) {
            settingsBinding.locationRadioGroup.check(R.id.radio_gps)
        }else{
            settingsBinding.locationRadioGroup.check(R.id.radio_map)
        }

        if(SettingsConstants.notificationState==SettingsConstants.NotificationState.OFF) {
            settingsBinding.notifiactionRadioGroup.check(R.id.radio_disable)
        }else{
            settingsBinding.notifiactionRadioGroup.check(R.id.radio_enable)
        }

        if(SettingsConstants.windSpeed==SettingsConstants.WindSpeed.MILE_HOUR) {
            settingsBinding.windRadioGroup.check(R.id.radio_mile_hour)
        }else{
            settingsBinding.windRadioGroup.check(R.id.radio_meter_sec)
        }

        when(SettingsConstants.temperature) {
            SettingsConstants.Temperature.F->{
                settingsBinding.tempRadioGroup.check(R.id.radio_fahrenheit)
            }
            SettingsConstants.Temperature.K->{
                settingsBinding.tempRadioGroup.check(R.id.radio_kelvin)
            }
            else->{
                settingsBinding.tempRadioGroup.check(R.id.radio_celsius)
            }
        }
    }
}