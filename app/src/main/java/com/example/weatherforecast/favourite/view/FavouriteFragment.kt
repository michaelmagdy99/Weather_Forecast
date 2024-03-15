package com.example.weatherforecast.favourite.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavouriteBinding
import com.example.weatherforecast.favourite.view_model.FavouriteViewModel
import com.example.weatherforecast.favourite.view_model.FavouriteViewModelFactory
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.utilities.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {

    private lateinit var favouriteBinding: FragmentFavouriteBinding


    private lateinit var favViewModel: FavouriteViewModel
    private lateinit var favViewModelFactory: FavouriteViewModelFactory

    private lateinit var favAdater: FavouriteAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        favouriteBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return favouriteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouriteBinding.fabAddPlace.setOnClickListener {
            val action = FavouriteFragmentDirections.actionFavouriteToMap()
            action.setFrom("fav")
            Navigation.findNavController(view).navigate(action)
        }


        favAdater = FavouriteAdapter(requireContext()) {
            val action = FavouriteFragmentDirections.actionFavouriteToHome()
            action.setFavLocation(it)
            Navigation.findNavController(view).navigate(action)
        }

        layoutManager = LinearLayoutManager(context)

        favouriteBinding.favRc.layoutManager = layoutManager

        favouriteBinding.favRc.adapter = favAdater

        favViewModelFactory = FavouriteViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource.getInstance(requireContext())
            )
        )
        favViewModel =
            ViewModelProvider(this, favViewModelFactory).get(FavouriteViewModel::class.java)

        lifecycleScope.launch(Dispatchers.Main) {
            favViewModel.locationList.collectLatest {
                when (it) {
                    is UiState.Success -> {
                        if (it.data.isEmpty()){
                            favouriteBinding.favRc.visibility = View.GONE
                            favouriteBinding.emptyFav.visibility = View.VISIBLE
                            favouriteBinding.noPlaceTv.visibility = View.VISIBLE
                        }else{
                            favouriteBinding.favRc.visibility = View.VISIBLE
                            favouriteBinding.emptyFav.visibility = View.GONE
                            favouriteBinding.noPlaceTv.visibility = View.GONE
                            favAdater.submitList(it.data)
                        }
                    }

                    is UiState.Failed -> {
                        favouriteBinding.favRc.visibility = View.GONE
                        favouriteBinding.emptyFav.visibility = View.VISIBLE
                        favouriteBinding.noPlaceTv.visibility = View.VISIBLE
                    }

                    is UiState.Loading -> {
                        Log.i("TAG", "onViewCreated: loading")
                        favouriteBinding.favRc.visibility = View.GONE
                        favouriteBinding.emptyFav.visibility = View.VISIBLE
                        favouriteBinding.noPlaceTv.visibility = View.VISIBLE
                    }

                    else -> {
                        Log.i("TAG", "onViewCreated: else")
                    }
                }
            }
        }


        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,  ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val location = favAdater.currentList[position]
                    checkDeleteDialog(location)
                }
            }
        )
        itemTouchHelper.attachToRecyclerView(favouriteBinding.favRc)

    }


    private fun checkDeleteDialog(location: FaviourateLocationDto) {
        val deleteAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        deleteAlertDialogBuilder
            .setTitle(getString(R.string.deleted_from_alert))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                favViewModel.deleteLocation(location)
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                favViewModel.getAllLocation()
            }
            .show()

    }
}

