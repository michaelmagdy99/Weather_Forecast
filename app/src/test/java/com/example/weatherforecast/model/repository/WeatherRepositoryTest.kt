package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.database.FakeWeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.LocationKey
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.FakeWeatherRemoteDataSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.EasyMock2Matchers.equalTo
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest{

    private lateinit var weatherRemoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var weatherLocalDataSource: FakeWeatherLocalDataSource
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        weatherRemoteDataSource = FakeWeatherRemoteDataSource()
        weatherLocalDataSource = FakeWeatherLocalDataSource()
        weatherRepository = WeatherRepository(weatherRemoteDataSource, weatherLocalDataSource)
    }

    @Test
    fun getCurrentWeather() = runBlockingTest {
        // Given
        val lat = 29.54213
        val lon = 30.15546

        // When
        val result = weatherRepository.getCurrentWeather(lat, lon)

        // Then
        val expectedResult = WeatherResponse(lat = lat, lon = lon)
        result.collect {
            assertThat(it, IsEqual(expectedResult))
        }
    }

    @Test
    fun insertFavWeatherInToDataBase() = runBlockingTest{
        //Given
        val favLocation = FaviourateLocationDto(LocationKey(29.3059751, 30.8549351), "Fayoum, Egypt", "24")
        //when
        weatherRepository.insertLocation(favLocation)

        // Then
        val result = weatherRepository.getLocalAllLocation().first()
        assertThat(result[0].locationKey, `is`(favLocation.locationKey))
    }



    // delete Favorite Location test
    @Test
    fun deleteFavWeather() = runBlockingTest{
        //Given
        val favLocation = FaviourateLocationDto(LocationKey(29.3059751, 30.8549351), "Fayoum, Egypt", "24")
        weatherRepository.insertLocation(favLocation)

        //when
        weatherRepository.deleteLocation(favLocation)

        // Then
        val result = weatherRepository.getLocalAllLocation()
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
        weatherRepository.insertAlert(alert)

        // Then
        val result = weatherRepository.getListOfAlerts().first()
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
        weatherRepository.insertAlert(alert)

        // When
        weatherRepository.deleteAlert(alert)

        // Then
        val result = weatherRepository.getListOfAlerts().first()
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
        weatherRepository.insertAlert(alert)

        // When
        val result = weatherRepository.getAlertWithId(alert.id)

        // Then
        assertThat(result, `is`(alert))
    }


}