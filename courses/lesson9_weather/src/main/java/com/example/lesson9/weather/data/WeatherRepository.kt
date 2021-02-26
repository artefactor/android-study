package com.example.lesson9.weather.data

import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import io.reactivex.Single

interface WeatherRepository {
    fun getCurrentWeatherData(
            city: String,
            lang: String = "ru",
    ): Single<WeatherRawDataRoot>

    fun getHourlyForecast2days(
            lat: String,
            lon: String,
            lang: String = "ru",
    ): Single<WeatherRawDataOneCallRoot>
}
