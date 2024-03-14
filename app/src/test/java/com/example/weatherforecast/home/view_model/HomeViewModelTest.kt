package com.example.weatherforecast.home.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.MainDispatcherRule
import com.example.weatherforecast.model.repository.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var repo : FakeWeatherRepository

    @Before
    fun setUp(){
        repo = FakeWeatherRepository()
        viewModel = HomeViewModel(repo)
    }
    @Test
    fun testGetCurrentWeather_Success() = runBlockingTest   {
        // Given
        val lat = 29.54213
        val lon = 30.15546

        // When
        viewModel.getCurrentWeather(lat, lon)


        // Then
        val result = viewModel.weatherStateFlow.first()
        assertThat(result , not(nullValue()))
    }


    @Test
    fun testGetFavoriteWeather_Success() = runBlockingTest   {
        // Given
        val lat = 29.54213
        val lon = 30.15546

        // When
        viewModel.getCurrentWeather(lat, lon)


        // Then
        val result = viewModel.weatherStateFlow.first()
        assertThat(result , not(nullValue()))
    }

}