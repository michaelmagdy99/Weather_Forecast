package com.example.weatherforecast.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    lateinit var settingsBinding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        settingsBinding = FragmentSettingsBinding.inflate(inflater,container,false)

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}