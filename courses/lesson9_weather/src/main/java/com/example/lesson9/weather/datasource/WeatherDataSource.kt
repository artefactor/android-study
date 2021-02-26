package com.example.lesson9.weather.datasource

import io.reactivex.Single

interface WeatherDataSource {
    fun getCurrentWeatherData(city: String, lang: String): Single<WeatherRawDataRoot>
    fun getHourlyForecast2days(lat: String, lon: String, lang: String): Single<WeatherRawDataOneCallRoot>

}
