package com.example.lesson9.weather.domain

import android.content.Context
import com.example.lesson9.weather.datasource.CityEntity
import com.example.lesson9.weather.data.WeatherRepository
import com.example.lesson9.weather.data.WeatherRepositoryImpl
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import com.example.lesson9.weather.presentation.WeatherListMapper
import com.example.lesson9.weather.presentation.WeatherMapper
import io.reactivex.Completable
import io.reactivex.Single

class WeatherUseCaseImpl
(
        private val mapper: (WeatherRawDataRoot) -> WeatherDomainData = WeatherMapper(),
        private val mapper2: (WeatherRawDataOneCallRoot, String, String) -> List<WeatherDomainData> = WeatherListMapper(),
) : WeatherUseCase {
    private lateinit var weatherRepository: WeatherRepository

    //
    // TODO Denis, для репозитория нужен контекст. Я его так получаю. Как принято это делать?
    override fun init(context: Context) {
        weatherRepository = WeatherRepositoryImpl(context)
    }

    override fun getAllCities(): Single<List<CityEntity>> = weatherRepository.getAllCities()
    override fun addCity(city: String, country: String, lat: String, lon: String): Single<Long> =
            weatherRepository.addCity(CityEntity(0, city, country, lat, lon))

    override fun getCity(cityId: Long): Single<CityEntity> = weatherRepository.getCity(cityId)
    override fun updateCity(city: CityEntity): Completable = weatherRepository.updateCity(city)


    override fun getCurrentWeatherData(city: String, lang: String): Single<WeatherDomainData> =
            weatherRepository.getCurrentWeatherData(city)
                    .map { data -> mapper(data) }

    override fun getHourlyForecast2days(city: String, country: String, lat: String, lon: String, lang: String): Single<List<WeatherDomainData>> =
            weatherRepository.getHourlyForecast2days(lat, lon)
                    .map { data -> mapper2(data, city, country) }
}