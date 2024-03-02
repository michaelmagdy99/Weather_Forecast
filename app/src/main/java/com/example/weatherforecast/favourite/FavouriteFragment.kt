package com.example.weatherforecast.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavouriteBinding
import com.example.weatherforecast.databinding.FragmentHomeBinding

class FavouriteFragment : Fragment() {

    private lateinit var favouriteBinding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        favouriteBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }
}