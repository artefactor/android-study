package com.example.lesson9.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import com.example.lesson9.weather.presentation.FragmentWeather

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment();
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction()
                .add<FragmentWeather>(R.id.fragmentContainer, "WEATHER")
                .commit()
    }
}