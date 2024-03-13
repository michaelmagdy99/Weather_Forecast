package com.example.weatherforecast.alert.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherforecast.R
import com.example.weatherforecast.alert.AlertUtils
import com.example.weatherforecast.alert.view_model.AlertViewModel
import com.example.weatherforecast.alert.view_model.AlertViewModelFactory
import com.example.weatherforecast.alert.work_manager.AlertWorker
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.LocationKey
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import com.example.weatherforecast.utilities.ApiState
import com.example.weatherforecast.utilities.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment()  {

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0

    private lateinit var alertBinding: FragmentAlertBinding

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory

    private lateinit var alertAdapter: AlertAdapter
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        alertBinding = FragmentAlertBinding.inflate(inflater, container, false)
        return alertBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertAdapter = AlertAdapter(requireContext()) {
            alertViewModel.deleteAlert(it)
        }
        layoutManager = LinearLayoutManager(context)

        alertBinding.alertRc.layoutManager = layoutManager

        alertBinding.alertRc.adapter = alertAdapter

        alertViewModelFactory = AlertViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource.getInstance(requireContext()), requireContext()
            )
        )
        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

        alertBinding.fabAddAlert.setOnClickListener {
            showEditAlertDialog()
        }

        alertViewModel.getAllAlerts()

        lifecycleScope.launch(Dispatchers.Main) {
            alertViewModel.alerts.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        if (it.data.isEmpty()){
                            alertBinding.alertRc.visibility = View.GONE
                            alertBinding.emptyAlert.visibility = View.VISIBLE
                            alertBinding.noAlert.visibility = View.VISIBLE
                        }else{
                            alertBinding.alertRc.visibility = View.VISIBLE
                            alertBinding.emptyAlert.visibility = View.GONE
                            alertBinding.noAlert.visibility = View.GONE
                            alertAdapter.submitList(it.data)
                        }
                    }

                    is ApiState.Failed -> {
                        alertBinding.alertRc.visibility = View.GONE
                        alertBinding.emptyAlert.visibility = View.VISIBLE
                        alertBinding.noAlert.visibility = View.VISIBLE
                    }

                    is ApiState.Loading -> {
                        Log.i("TAG", "onViewCreated: loading")
                        alertBinding.alertRc.visibility = View.GONE
                        alertBinding.emptyAlert.visibility = View.VISIBLE
                        alertBinding.noAlert.visibility = View.VISIBLE
                    }

                    else -> {
                        Log.i("TAG", "onViewCreated: else")
                    }
                }
            }
        }
    }

    private fun showEditAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.edit_alert_dialog, null)
        val button = view.findViewById<Button>(R.id.save_alert)
        val radioGroup = view.findViewById<RadioGroup>(R.id.alert_radio_group)
        val fromCardView = view.findViewById<CardView>(R.id.from_card_view)
        val toCardView = view.findViewById<CardView>(R.id.to_card_view)

        val fromTime = view.findViewById<TextView>(R.id.clock_from_tv)
        val toTime = view.findViewById<TextView>(R.id.clock_to_tv)
        val toDate = view.findViewById<TextView>(R.id.date_to_tv)
        val fromDate = view.findViewById<TextView>(R.id.date_from_tv)

        builder.setView(view)
        builder.setCancelable(false)

        val alertDialog = builder.create()

        fromCardView.setOnClickListener{
            openDateAndTimePicker(fromTime , fromDate)
        }
        toCardView.setOnClickListener {
            openDateAndTimePicker(toTime , toDate)
        }

        button.setOnClickListener {
            // insert data in data base


            val selectedOptionId = radioGroup.checkedRadioButtonId

            when (selectedOptionId) {
                R.id.radio_alarm -> {
                }

                R.id.radio_notification -> {

                }
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }



    private fun openDateAndTimePicker(time: TextView, date:TextView) {
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            R.style.MyDatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                myDay = dayOfMonth
                myYear = year
                myMonth = month
                val calendar: Calendar = Calendar.getInstance()
                hour = calendar.get(Calendar.HOUR_OF_DAY)
                minute = calendar.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    requireActivity(),
                    R.style.MyTimePickerDialogTheme,
                    { _, hourOfDay, minute ->
                        myHour = hourOfDay
                        myMinute = minute
                        val selectedDateTime = Calendar.getInstance()
                        selectedDateTime.set(myYear, myMonth, myDay, myHour, myMinute)
                        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(selectedDateTime.time)
                        val formattedTime = timeFormat.format(selectedDateTime.time)
                        time.text = formattedTime
                        date.text = formattedDateTime
                    },
                    hour,
                    minute,
                    DateFormat.is24HourFormat(requireActivity())
                )
                timePickerDialog.show()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

}