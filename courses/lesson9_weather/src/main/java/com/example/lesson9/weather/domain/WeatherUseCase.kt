package com.example.lesson9.weather.domain

import io.reactivex.Single

interface WeatherUseCase {
    fun getCurrentWeatherData(
            city: String,
            lang: String = "ru",
    ): Single<WeatherDomainData>

    fun getHourlyForecast2days(
            city: String,
            lat: String,
            lon: String,
            lang: String = "ru",
    ): Single<List<WeatherDomainData>>

}