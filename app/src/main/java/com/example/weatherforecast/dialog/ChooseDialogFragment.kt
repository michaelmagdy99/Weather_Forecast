package com.example.weatherforecast.dialog

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentChooseDialogBinding
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import com.example.weatherforecast.utilities.SettingsConstants

class ChooseDialogFragment : Fragment() {

    private lateinit var dialogBinding: FragmentChooseDialogBinding
    private lateinit var sharedPreferences: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialogBinding = FragmentChooseDialogBinding.inflate(inflater, container, false)
        return dialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = SharedPreferencesHelper.getInstance(requireActivity())

        if (!sharedPreferences.isDialogShown()) {
            sharedPreferences.setDialogShown(true)
            showDialog()
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val viewInflater = layoutInflater.inflate(R.layout.fragment_initial_dialog, null)
        val button = viewInflater.findViewById<Button>(R.id.btn_ok)
        val radioGroup = viewInflater.findViewById<RadioGroup>(R.id.radioGroup)

        builder.setView(viewInflater)
        builder.setCancelable(false)

        val alertDialog = builder.create()

        button.setOnClickListener {
            val selectedOptionId = radioGroup.checkedRadioButtonId

            when (selectedOptionId) {
                R.id.radio_gps -> {
                    SettingsConstants.location = SettingsConstants.Location.GPS
                    navigateToHome()
                }

                R.id.radio_maps -> {
                    SettingsConstants.location = SettingsConstants.Location.MAP
                    navigateToMap()
                }
            }
            val pref = SharedPreferencesHelper(requireContext())
            pref.insertInData()
            pref.saveAsNewSetting(1)
            requireActivity().recreate()

            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun navigateToHome() {
        val action = ChooseDialogFragmentDirections.actionChooseDialogFragmentToHome()
        navigate(action)
    }

    private fun navigateToMap() {
        val action = ChooseDialogFragmentDirections.actionChooseDialogFragmentToMap()
        action.setFrom("map")
        navigate(action)
    }

    private fun navigate(direction: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(direction) }
    }
}
