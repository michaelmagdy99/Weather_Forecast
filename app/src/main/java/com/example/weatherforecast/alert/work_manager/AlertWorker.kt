package com.example.weatherforecast.alert.work_manager

import android.content.Context
import android.graphics.PixelFormat
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weatherforecast.R
import com.example.weatherforecast.alert.AlertUtils
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.utilities.Converts
import com.example.weatherforecast.utilities.LocationUtils
import com.example.weatherforecast.utilities.SettingsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AlertWorker(private val appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val alertId = inputData.getString("id")

        val repo = WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource.getInstance(appContext)
        )

        return withContext(Dispatchers.IO) {
            if (alertId != null) {
                try {
                    val alert = repo.getAlertWithId(alertId)

                    val weather = repo.getCurrentWeather(
                        alert.lat,
                        alert.lon,
                    )

                    weather.catch { e ->
                        Log.e("AlertWorker", "Network request failed: $e")
                    }.collect { weatherData ->
                        Log.i("AlertWorker", "Received weather data: ${weatherData.timezone}")

                        when (alert.alertType) {
                            AlertType.ALARM -> createAlarm(appContext, alert, Converts.getTemperature(
                                weatherData.current?.temp?.toInt() ?: 0
                            ) , weatherData.current?.weather?.get(0)?.description)
                            AlertType.NOTIFICATION -> AlertUtils.sendNotification(appContext, alert)
                        }
                    }

                    Result.success()
                } catch (e: Exception) {
                    Log.e("AlertWorker", "Error processing work: $e")
                    Result.failure()
                }
            } else {
                Result.failure()
            }
        }
    }


    private suspend fun createAlarm(context: Context, message: Alert, tempValue: Int, descValue: String?) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)

        val view: View =
            LayoutInflater.from(context).inflate(R.layout.fragment_alarm, null, false)
        val dismissBtn = view.findViewById<Button>(R.id.dismiss_btn)
        val textView = view.findViewById<TextView>(R.id.message)
        val temp = view.findViewById<TextView>(R.id.temp_weather)
        val desc = view.findViewById<TextView>(R.id.text_description_weather)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.TOP

        val location = Location("").apply {
            latitude = message.lat ?: 0.0
            longitude = message.lon ?: 0.0
        }

        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        withContext(Dispatchers.Main) {
            windowManager.addView(view, layoutParams)
            view.visibility = View.VISIBLE
            textView.text = LocationUtils.getAddress(context, location)
            temp.text = tempValue.toString() +"\u00B0"+ SettingsConstants.getTemp().toString()
            desc.text = descValue.toString()
        }

        mediaPlayer.start()
        mediaPlayer.isLooping = true
        dismissBtn.setOnClickListener {
            mediaPlayer?.release()
            windowManager.removeView(view)
        }
    }

}
object AlertType {
    const val NOTIFICATION = "NOTIFICATION"
    const val ALARM = "ALARM"
}
