package com.example.lesson9.weather.data

import android.content.Context
import com.example.lesson9.weather.datasource.CityDataSource
import com.example.lesson9.weather.datasource.CityDataSourceImpl
import com.example.lesson9.weather.datasource.CityEntity
import com.example.lesson9.weather.datasource.WeatherDataSource
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import com.example.lesson9.weather.datasource.network.OpenWeatherMapApiController
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class WeatherRepositoryImpl(context: Context) : WeatherRepository {
    private val weatherDataSource: WeatherDataSource = OpenWeatherMapApiController()
    private val cityDataSource: CityDataSource = CityDataSourceImpl(context)

    override fun getAllCities(): Single<List<CityEntity>> =
            Single.create<List<CityEntity>> {
                it.onSuccess(cityDataSource.getAllInfo())
            }.subscribeOn(Schedulers.io())

    override fun addCity(item: CityEntity): Single<Long> = Single.create<Long> {
        it.onSuccess(cityDataSource.add(item))
    }.subscribeOn(Schedulers.io())

    override fun getCity(id: Long): Single<CityEntity> =
            Single.create<CityEntity> {
                it.onSuccess(cityDataSource.getInfo(id))
            }.subscribeOn(Schedulers.io())

    override fun updateCity(item: CityEntity): Completable = Completable.create {
        cityDataSource.update(item)
        it.onComplete()
    }.subscribeOn(Schedulers.io())


    override fun getCurrentWeatherData(city: String, lang: String): Single<WeatherRawDataRoot> =
            weatherDataSource.getCurrentWeatherData(city, lang)

    override fun getHourlyForecast2days(lat: String, lon: String, lang: String): Single<WeatherRawDataOneCallRoot> =
            weatherDataSource.getHourlyForecast2days(lat, lon, lang)

}
