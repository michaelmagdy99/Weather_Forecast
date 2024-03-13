//package com.example.weatherforecast.alert.work_manager
//import android.content.Context
//import android.graphics.PixelFormat
//import android.media.MediaPlayer
//import android.os.Build
//import android.util.Log
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.WindowManager
//import android.widget.Button
//import android.widget.TextView
//import androidx.work.WorkManager
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.example.weatherforecast.R
//import com.example.weatherforecast.alert.AlertUtils
//import com.example.weatherforecast.model.database.WeatherLocalDataSource
//import com.example.weatherforecast.model.dto.Alert
//import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
//import com.example.weatherforecast.model.repository.WeatherRepository
//import com.example.weatherforecast.utilities.LocationUtils
//import com.example.weatherforecast.utilities.SettingsConstants
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.withContext
//import java.util.Calendar
//
//class AlertWorker(private val appContext: Context, workerParams: WorkerParameters) :
//    Worker(appContext, workerParams) {
//
//    override suspend fun doWork(): Result {
//        val id = inputData.getString("input")
//        Log.d("TAG", "doWork1: $id")
//        return try {
//            if (id != null) {
//                val repository = WeatherRepository.getInstance(
//                    WeatherRemoteDataSource.getInstance(),
//                    WeatherLocalDataSource.getInstance(appContext),
//                    appContext
//                )
//                val alert = repository.getAlertWithId(id)
//                val responseFlow = repository.getFavouriteWeather(
//                    alert.lat,
//                    alert.lon,
//                    SettingsConstants.getLang(),
//                    "metric"
//                )
//
//                responseFlow.collect { response ->
//                    if (response != null) {
//                        val alerts = response.alerts
//                        if (alerts != null) {
//                            val alertsEvent: String = buildString {
//                                for (a in alerts) {
//                                    append(a.event)
//                                    append("\n")
//                                }
//                            }
//                            when (alert.kind) {
//                                AlertKind.ALARM -> createAlarm(appContext, alertsEvent)
//                                AlertKind.NOTIFICATION -> AlertUtils.sendNotification(appContext, alertsEvent)
//                            }
//                        } else {
//                            LocationUtils.getAddress(
//                                appContext,
//                                alert.lon,
//                                alert.lat
//                            ) { address ->
//                                when (alert.kind) {
//                                    AlertKind.ALARM -> createAlarm(
//                                        appContext,
//                                        appContext.getString(
//                                            R.string.mostly_cloud,
//                                            formatAddressToCity(address)
//                                        )
//                                    )
//                                    AlertKind.NOTIFICATION -> AlertUtils.sendNotification(
//                                        appContext,
//                                        appContext.getString(
//                                            R.string.mostly_cloud,
//                                            formatAddressToCity(address)
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                        removeFromDataBaseAndDismiss(repository, alert, appContext)
//                        Result.success()
//                    } else {
//                        Result.failure()
//                    }
//                }
//            } else {
//                Result.failure()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("TAG", "doWork: $e")
//            Result.failure()
//        }
//    }
//
//    private suspend fun removeFromDataBaseAndDismiss(
//        repo: WeatherRepository,
//        alert: Alert,
//        appContext: Context
//    ) {
//        val _Day_TIME_IN_MILLISECOND = 24 * 60 * 60 * 1000L
//        val now = Calendar.getInstance().timeInMillis
//        if ((alert.to - now) < _Day_TIME_IN_MILLISECOND) {
//            WorkManager.getInstance(appContext).cancelAllWorkByTag(alert.id)
//            repo.deleteAlert(alert)
//        }
//    }
//
//    private suspend fun createAlarm(context: Context, message: String) {
//        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
//
//        val view: View =
//            LayoutInflater.from(context).inflate(R.layout.fragment_alarm, null, false)
//        val dismissBtn = view.findViewById<Button>(R.id.dismiss_btn)
//        val textView = view.findViewById<TextView>(R.id.message)
//        val layoutParams = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.TRANSLUCENT
//        )
//        layoutParams.gravity = Gravity.CENTER
//
//        val windowManager =
//            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//
//        withContext(Dispatchers.Main) {
//            windowManager.addView(view, layoutParams)
//            view.visibility = View.VISIBLE
//            textView.text = message
//        }
//
//        mediaPlayer.start()
//        mediaPlayer.isLooping = true
//        dismissBtn.setOnClickListener {
//            mediaPlayer?.release()
//            windowManager.removeView(view)
//        }
//    }
//}
//
//object AlertKind {
//    const val NOTIFICATION = "NOTIFICATION"
//    const val ALARM = "ALARM"
//}
