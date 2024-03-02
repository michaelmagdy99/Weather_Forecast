package com.example.weatherforecast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherforecast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var homeActivityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeActivityMainBinding.root)
    }
}