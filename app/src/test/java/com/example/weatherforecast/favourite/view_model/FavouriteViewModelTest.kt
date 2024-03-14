package com.example.weatherforecast.favourite.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.MainDispatcherRule
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.LocationKey
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
class FavouriteViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: FavouriteViewModel
    private lateinit var repo : FakeWeatherRepository

    @Before
    fun setUp(){
        repo = FakeWeatherRepository()
        viewModel = FavouriteViewModel(repo)
    }


    @Test
    fun testGetAllLocalLocation() = runBlockingTest {
        // Given
        val locationList = listOf(
            FaviourateLocationDto(LocationKey(29.54213, 30.15546),"Fayoum", "0"),
            FaviourateLocationDto(LocationKey(29.54213, 30.15546),"Fayoum", "0")
        )
        locationList.forEach{
            viewModel.insertLocation(it)
        }

        // When
        viewModel.getAllProduct()

        // Then
        val result = viewModel.locationList.value
        assertThat(result, not(nullValue()))
    }

    @Test
    fun testInsertFavLocation() = runBlockingTest{
        //Given
        val favLocation = FaviourateLocationDto(LocationKey(29.3059751, 30.8549351), "Fayoum, Egypt", "24")
        //when
        viewModel.insertLocation(favLocation)

        // Then
        val result = viewModel.locationList.value
        assertThat(result, not(nullValue()))
    }




    @Test
    fun testDeleteFavLocation(){
        //Given
        val favLocation = FaviourateLocationDto(LocationKey(29.3059751, 30.8549351), "Fayoum, Egypt", "24")
        viewModel.insertLocation(favLocation)

        //when
        viewModel.deleteLocation(favLocation)

        // Then
        val result = viewModel.locationList.value
        assertThat(result, not(nullValue()))
    }


}