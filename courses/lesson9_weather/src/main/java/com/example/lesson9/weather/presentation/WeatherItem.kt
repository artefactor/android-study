package com.example.lesson9.weather.presentation

data class WeatherItem(
        val city: String,
        val country: String,
        val date: String,
        val title: String,
        val temp: String,
        val icon: String,
        val lon: String,
        val lat: String,
)