package com.example.lesson9.weather.presentation

import com.example.lesson9.weather.domain.WeatherDomainData

interface WeatherPresenter {
    fun fetchCurrentWeather(city: String)
    fun fetchForecast(lat: String, lon: String, city: String)
    fun close()
}


interface WeatherView {
    fun showCurrentWeather(data: WeatherDomainData)
    fun showForecast(data: List<WeatherDomainData>)
    fun showError(errorMessage: String)
}

