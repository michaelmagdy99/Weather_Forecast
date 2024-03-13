package com.example.weatherforecast.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforecast.model.database.WeatherDatabase
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.LocationKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDoaTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherDatabase: WeatherDatabase
    @Before
    fun setUpDatabase(){
        weatherDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),WeatherDatabase::class.java).build()
    }

    @After
    fun closDB(){
        weatherDatabase.close()
    }

    // insert Favorite Location test
    @Test
    fun insertFavWeather() = runBlockingTest{
        //Given
        val favLocation = FaviourateLocationDto(LocationKey(29.3059751, 30.8549351), "Fayoum, Egypt", "24")
        //when
        weatherDatabase.getWeather().insertLocation(favLocation)

        // Then
        val result = weatherDatabase.getWeather().getAllLocation().first()
        assertThat(result[0].locationKey, `is`(favLocation.locationKey))
    }


    // delete Favorite Location test
    @Test
    fun deleteFavWeather() = runBlockingTest{
        //Given
        val favLocation = FaviourateLocationDto(LocationKey(29.3059751, 30.8549351), "Fayoum, Egypt", "24")
        weatherDatabase.getWeather().insertLocation(favLocation)

        //when
        weatherDatabase.getWeather().delete(favLocation)

        // Then
        val result = weatherDatabase.getWeather().getAllLocation()
        assertThat(result.first().isEmpty(), `is`(true))
    }


    // Insert Alert test
    @Test
    fun insertAlert() = runBlockingTest {
        // Given
        val alert = Alert(
            id = "1",
            fromDate = "2022-03-10",
            toDate = "2022-03-12",
            fromTime = "08:00",
            toTime = "10:00",
            alertType = "ALARM",
            lat = 29.3059751,
            lon = 30.8549351
        )

        // When
        weatherDatabase.getWeather().insertAlert(alert)

        // Then
        val result = weatherDatabase.getWeather().getListOfAlerts().first()
        assertThat(result[0], `is`(alert))
    }

    // Delete Alert test
    @Test
    fun deleteAlert() = runBlockingTest {
        // Given
        val alert = Alert(
            id = "1",
            fromDate = "2022-03-10",
            toDate = "2022-03-12",
            fromTime = "08:00",
            toTime = "10:00",
            alertType = "ALARM",
            lat = 29.3059751,
            lon = 30.8549351
        )
        weatherDatabase.getWeather().insertAlert(alert)

        // When
        weatherDatabase.getWeather().deleteAlert(alert)

        // Then
        val result = weatherDatabase.getWeather().getListOfAlerts().first()
        assertThat(result.isEmpty(), `is`(true))
    }


    @Test
    fun getAlertWithId() = runBlockingTest {
        // Given
        val alert = Alert(
            id = "1",
            fromDate = "2022-03-10",
            toDate = "2022-03-12",
            fromTime = "08:00",
            toTime = "10:00",
            alertType = "ALARM",
            lat = 29.3059751,
            lon = 30.8549351
        )
        weatherDatabase.getWeather().insertAlert(alert)

        // When
        val result = weatherDatabase.getWeather().getAlertWithId(alert.id)

        // Then
        assertThat(result, `is`(alert))
    }
}