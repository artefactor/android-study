package com.example.lesson9.weather.domain

import com.example.lesson9.weather.data.WeatherRepository
import com.example.lesson9.weather.data.WeatherRepositoryImpl
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import com.example.lesson9.weather.presentation.WeatherListMapper
import com.example.lesson9.weather.presentation.WeatherMapper
import io.reactivex.Single

class WeatherUseCaseImpl
(
        private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(),
        private val mapper: (WeatherRawDataRoot) -> WeatherDomainData = WeatherMapper(),
        private val mapper2: (WeatherRawDataOneCallRoot, String) -> List<WeatherDomainData> = WeatherListMapper(),
) : WeatherUseCase {

    override fun getCurrentWeatherData(city: String, lang: String): Single<WeatherDomainData> =
            weatherRepository.getCurrentWeatherData(city)
                    .map { data -> mapper(data) }

    override fun getHourlyForecast2days(city: String, lat: String, lon: String, lang: String): Single<List<WeatherDomainData>> =
            weatherRepository.getHourlyForecast2days(lat, lon)
                    .map { data -> mapper2(data, city) }
}