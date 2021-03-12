package com.example.lesson9.weather.domain

import android.content.Context
import com.example.lesson9.weather.datasource.CityEntity
import io.reactivex.Completable
import io.reactivex.Single

interface WeatherUseCase {
    fun getCurrentWeatherData(
            city: String,
            lang: String = "ru",
    ): Single<WeatherDomainData>

    fun getHourlyForecast2days(
            city: String,
            country: String,
            lat: String,
            lon: String,
            lang: String = "ru",
    ): Single<List<WeatherDomainData>>

    fun init(context: Context)

    fun getAllCities(): Single<List<CityEntity>>
    fun addCity(city: String, country: String, lat: String, lon: String): Single<Long>
    fun getCity(cityId: Long): Single<CityEntity>
    fun updateCity(city: CityEntity): Completable
}