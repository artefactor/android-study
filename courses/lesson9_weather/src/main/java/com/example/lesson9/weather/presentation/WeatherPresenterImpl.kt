package com.example.lesson9.weather.presentation

import android.util.Log
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.data.WeatherRepository
import com.example.lesson9.weather.data.WeatherRepositoryImpl
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import com.example.lesson9.weather.domain.WeatherDomainData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class WeatherPresenterImpl(
        private val compositeDisposable: CompositeDisposable = CompositeDisposable(),
        private val weatherView: WeatherView,
        private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(),
        private val mapper: (WeatherRawDataRoot) -> WeatherDomainData = WeatherMapper(),
        private val mapper2: (WeatherRawDataOneCallRoot, String) -> List<WeatherDomainData> = WeatherListMapper(),
) : WeatherPresenter {

    override fun fetchCurrentWeather(city:String) {
        weatherRepository.getCurrentWeatherData(city)
                .map { data -> mapper(data) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data -> weatherView.showCurrentWeather(data) },
                        { error ->
                            weatherView.showError(error.toString())
                            Log.e(LOG_TAG, "exception during fetch data", error)
                        }
                ).also { compositeDisposable.add(it) }
    }

    override fun fetchForecast(lat: String, lon: String, city: String) {
        weatherRepository.getHourlyForecast2days(lat, lon)
                .map { data -> mapper2(data, city) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data -> weatherView.showForecast(data) },
                        { error ->
                            weatherView.showError(error.toString())
                            Log.e(LOG_TAG, "exception during fetch data", error)
                        }
                ).also { compositeDisposable.add(it) }
    }

    override fun close() {
        compositeDisposable.clear()
    }

}
