package com.example.weatherforecast.alert

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentAlertBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val CHANNEL_ID = "3012"

const val NOTIFICATION_ID = 2006
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

        alertBinding.fabAddAlert.setOnClickListener {
            showEditAlertDialog()
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

            val selectedOptionId = radioGroup.checkedRadioButtonId

            when (selectedOptionId) {
                R.id.radio_alarm -> {

                }

                R.id.radio_notification -> {
                    val notificationManager = NotificationManagerCompat.from(requireActivity())
                    val isNotificationPermissionGranted = notificationManager.areNotificationsEnabled()
                    if (!isNotificationPermissionGranted) {
                        showNotificationPermissionDialog()
                    } else {
                        createNotificationChannel()
                        sendNotification()
                    }
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

    private fun sendNotification() {
        val builder : NotificationCompat.Builder =
            NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("El-Kaff Hena")
            .setContentText("Wal3 Wal3")
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        with(NotificationManagerCompat.from(requireActivity()))
        {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID,builder.build())
        }

    }
    private fun showNotificationPermissionDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.weather))
            .setMessage(getString(R.string.mostly_cloud))
            .setPositiveButton(getString(R.string.disable)) { dialog: DialogInterface, _: Int ->
                openAppSettings()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dismiss)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = alertDialogBuilder.create()
        dialog.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel Name"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID.toString(), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = requireActivity().getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}