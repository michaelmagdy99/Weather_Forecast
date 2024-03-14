package com.example.weatherforecast.alert.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.MainDispatcherRule
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.repository.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AlertViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AlertViewModel
    private lateinit var repo : FakeWeatherRepository

    @Before
    fun setUp(){
        repo = FakeWeatherRepository()
        viewModel = AlertViewModel(repo)
    }


    @Test
    fun testGetAllLocalLocation() = runBlockingTest {
        // Given
        val alertsList = listOf(
            Alert(
                id = "1",
                fromDate = "2022-03-10",
                toDate = "2022-03-12",
                fromTime = "08:00",
                toTime = "10:00",
                alertType = "ALARM",
                lat = 29.3059751,
                lon = 30.8549351
            ),
            Alert(
                id = "1",
                fromDate = "2022-03-10",
                toDate = "2022-03-12",
                fromTime = "08:00",
                toTime = "10:00",
                alertType = "ALARM",
                lat = 29.3059751,
                lon = 30.8549351
            )
        )
        alertsList.forEach{
            viewModel.insertAlert(it)
        }

        // When
        viewModel.getAllAlerts()

        // Then
        val result = viewModel.alerts.value
        assertThat(result, not(nullValue()))
    }

    @Test
    fun testInsertFavLocation() = runBlockingTest{
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
        viewModel.insertAlert(alert)

        // Then
        val result = viewModel.alerts.value
        assertThat(result, not(nullValue()))
    }




    @Test
    fun testDeleteFavLocation() {
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
        viewModel.deleteAlert(alert)

        // Then
        val result = viewModel.alerts.value
        assertThat(result, not(nullValue()))
    }



}