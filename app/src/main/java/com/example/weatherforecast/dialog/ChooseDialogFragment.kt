package com.example.weatherforecast.dialog

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.navigation.Navigation
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentChooseDialogBinding
import com.example.weatherforecast.utilities.SettingsConstants


class ChooseDialogFragment : Fragment() {

    lateinit var dialogBinding: FragmentChooseDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialogBinding = FragmentChooseDialogBinding.inflate(inflater, container, false)
        showDialog()
        return dialogBinding.root
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.fragment_initial_dialog, null)
        val button = view.findViewById<Button>(R.id.btn_ok)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        builder.setView(view)
        builder.setCancelable(false)

        val alertDialog = builder.create()

        button.setOnClickListener {
            val selectedOptionId = radioGroup.checkedRadioButtonId

            when (selectedOptionId) {
                R.id.radio_gps -> {
                    SettingsConstants.location = SettingsConstants.Location.GPS
                    val action = ChooseDialogFragmentDirections.actionChooseDialogFragmentToHome()
                    action.setDestinationDescription("current")
                    Navigation.findNavController(requireView()).navigate(action)
                }

                R.id.radio_maps -> {
                    SettingsConstants.location = SettingsConstants.Location.MAP
                    val action = ChooseDialogFragmentDirections.actionChooseDialogFragmentToMap()
                    action.setFrom("map")
                    Navigation.findNavController(requireView()).navigate(action)                }
            }

            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}
