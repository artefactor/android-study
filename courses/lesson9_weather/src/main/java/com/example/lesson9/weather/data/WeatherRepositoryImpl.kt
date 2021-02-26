package com.example.lesson9.weather.data

import com.example.lesson9.weather.datasource.network.OpenWeatherMapApiController
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import com.example.lesson9.weather.datasource.WeatherDataSource
import io.reactivex.Single

class WeatherRepositoryImpl : WeatherRepository {
    private val weatherDataSource: WeatherDataSource = OpenWeatherMapApiController()

    override fun getCurrentWeatherData(city: String, lang: String): Single<WeatherRawDataRoot> =
            weatherDataSource.getCurrentWeatherData(city, lang)

    override fun getHourlyForecast2days(lat: String, lon: String, lang: String): Single<WeatherRawDataOneCallRoot> =
            weatherDataSource.getHourlyForecast2days(lat, lon, lang)

}
