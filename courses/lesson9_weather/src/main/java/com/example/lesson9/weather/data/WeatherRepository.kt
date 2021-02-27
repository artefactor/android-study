package com.example.lesson9.weather.data

import com.example.lesson9.weather.datasource.CityEntity
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import io.reactivex.Completable
import io.reactivex.Single

// TODO DEnis. скажи, здесь нужен общий репозиторий  или несколько?
interface WeatherRepository {
    fun getAllCities(): Single<List<CityEntity>>

    fun addCity(item: CityEntity): Single<Long>

    fun getCity(id: Long): Single<CityEntity>

    fun updateCity(item: CityEntity): Completable

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
