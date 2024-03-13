package com.example.weatherforecast.alert.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast.LENGTH_LONG
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weatherforecast.R
import com.example.weatherforecast.alert.AlertUtils
import com.example.weatherforecast.alert.reciver.AlarmReceiver
import com.example.weatherforecast.alert.view_model.AlertViewModel
import com.example.weatherforecast.alert.view_model.AlertViewModelFactory
import com.example.weatherforecast.alert.work_manager.AlertType
import com.example.weatherforecast.alert.work_manager.AlertWorker
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import com.example.weatherforecast.utilities.ApiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.log

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
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        alertBinding = FragmentAlertBinding.inflate(inflater, container, false)
        askAboutPermissions()
        return alertBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         alertAdapter = AlertAdapter(AlertAdapter.RemoveClickListener {
            WorkManager.getInstance(requireContext()).cancelAllWorkByTag(it.id)
            checkDeleteDialog(it)
        })

        layoutManager = LinearLayoutManager(context)

        alertBinding.alertRc.layoutManager = layoutManager

        alertBinding.alertRc.adapter = alertAdapter

        alertViewModelFactory = AlertViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource.getInstance(requireContext())
            )
        )
        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

        alertBinding.fabAddAlert.setOnClickListener {
            showEditAlertDialog()
        }

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
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

        val currentCalendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(currentCalendar.time)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = timeFormat.format(currentCalendar.time)
        fromDate.text = currentDate
        fromTime.text = currentTime

        toDate.text = currentDate
        toTime.text = currentTime

        builder.setView(view)
        builder.setCancelable(false)

        val alertDialog = builder.create()

        toCardView.setOnClickListener {
            openDateAndTimePicker(toTime , toDate)
        }

        button.setOnClickListener {

            val selectedOptionId = radioGroup.checkedRadioButtonId

            when (selectedOptionId) {
                R.id.radio_alarm -> {
                    val alertId = insertInDatabase(fromDate, fromTime, toDate, toTime, AlertType.ALARM)
                    scheduleAlarm(alertId, toDate.text.toString(), toTime.text.toString())
                }

                R.id.radio_notification -> {
                    val alertId = insertInDatabase(fromDate, fromTime, toDate, toTime, AlertType.ALARM)
                    val alert = Alert(
                        id = alertId,
                        fromDate = fromDate.text.toString(),
                        toDate = toDate.text.toString(),
                        fromTime = fromTime.text.toString(),
                        toTime = toTime.text.toString(),
                        alertType = AlertType.NOTIFICATION,
                        lat = SharedPreferencesHelper.getInstance(requireActivity()).loadCurrentLocation("lat")?.toDouble() ?: 0.0,
                        lon = SharedPreferencesHelper.getInstance(requireActivity()).loadCurrentLocation("long")?.toDouble() ?: 0.0
                    )
                    AlertUtils.sendNotification(requireContext(), alert)
                }
                else -> {

                }
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun insertInDatabase(
        fromDate: TextView,
        fromTime: TextView,
        toDate: TextView,
        toTime: TextView,
        alertType: String
    ): String {

        val alert = Alert(
        fromDate = fromDate.text.toString(),
        toDate = toDate.text.toString(),
        fromTime = fromTime.text.toString(),
        toTime = toTime.text.toString(),
        alertType = alertType,
        lat = SharedPreferencesHelper.getInstance(requireActivity()).loadCurrentLocation("lat")?.toDouble() ?: 0.0,
        lon = SharedPreferencesHelper.getInstance(requireActivity()).loadCurrentLocation("long")?.toDouble()?:0.0
        )
        alertViewModel.insertAlert(alert)

        return alert.id
    }

    private fun scheduleAlarm(alertId: String, selectedDate: String, selectedTime: String) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val selectedDateCalendar = Calendar.getInstance()
        selectedDateCalendar.time = dateFormat.parse(selectedDate)!!
        val selectedTimeCalendar = Calendar.getInstance()
        selectedTimeCalendar.time = timeFormat.parse(selectedTime)!!

        calendar.set(Calendar.YEAR, selectedDateCalendar.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, selectedDateCalendar.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, selectedDateCalendar.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, selectedTimeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, selectedTimeCalendar.get(Calendar.MINUTE))

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("id", alertId)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }


    private fun checkDisplayOverOtherAppPerm() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireContext().packageName)
            )
            someActivityResultLauncher.launch(intent)
        }
    }

    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Settings.canDrawOverlays(requireContext())) {
                Snackbar.make(
                    alertBinding.root,
                    getString(R.string.the_alarm_is_not_allow_to_show),
                    LENGTH_LONG
                ).setAction("Enable") {
                    sendToEnableIt()
                }.show()
            }
        }

    private fun sendToEnableIt() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireContext().packageName)
        )
        someActivityResultLauncher.launch(intent)
    }

    private fun askAboutPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            requireActivity().setShowWhenLocked(true)
            requireActivity().setTurnScreenOn(true)
        }
        if (!Settings.canDrawOverlays(requireActivity())) {
            checkDrawOverAppsPermissionsDialog()
        }
    }

    private fun checkDrawOverAppsPermissionsDialog() {
        AlertDialog.Builder(requireActivity()).setTitle(getString(R.string.permission_request))
            .setCancelable(false)
            .setMessage(getString(R.string.please_allow_draw_over_apps_permission))
            .setPositiveButton(
                "Yes"
            ) { _, _ -> checkDisplayOverOtherAppPerm() }.setNegativeButton(
                "No"
            ) { _, _ -> errorWarningForNotGivingDrawOverAppsPermissions()
            }.show()
    }


    private fun checkDeleteDialog(alert: Alert) {
        val deleteAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        deleteAlertDialogBuilder.setBackground(
            ResourcesCompat.getDrawable(
                resources, R.drawable.background_menu_drawer, requireActivity().theme
            )
        ).setTitle(getString(R.string.deleted_from_alert)).setCancelable(false)
            .setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                alertViewModel.deleteAlert(alert)
            }.setNegativeButton(
                getString(R.string.no)
            ) { _, _ -> }.show()
    }


    private fun errorWarningForNotGivingDrawOverAppsPermissions() {
        AlertDialog.Builder(requireActivity()).setTitle(getString(R.string.warning))
            .setCancelable(false).setMessage(
                getString(R.string.unfortunately_the_display_over_other_apps_permission_is_not_granted)
            ).setPositiveButton(android.R.string.ok) { _, _ -> }.show()
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