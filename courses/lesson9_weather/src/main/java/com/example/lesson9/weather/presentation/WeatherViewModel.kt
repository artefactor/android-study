package com.example.lesson9.weather.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.domain.WeatherDomainData
import com.example.lesson9.weather.domain.WeatherUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class WeatherViewModel(
        private val compositeDisposable: CompositeDisposable = CompositeDisposable(),
        private val mapper: (WeatherDomainData) -> WeatherItem = WeatherItemMapper(),
        private val weatherUseCase: WeatherUseCase,
) : ViewModel() {

    private val mutableWeatherLiveData = MutableLiveData<WeatherItem>()
    val weatherLiveData: LiveData<WeatherItem> = mutableWeatherLiveData

    private val mutableWeatherListLiveData = MutableLiveData<List<WeatherItem>>()
    val weatherListLiveData: LiveData<List<WeatherItem>> = mutableWeatherListLiveData

    private val mutableWeatherErrorLiveData = MutableLiveData<String>()
    val weatherErrorLiveData: LiveData<String> = mutableWeatherErrorLiveData

    fun init(context: Context) {
        weatherUseCase.init(context)
    }

    fun fetchCurrentWeather(city: String) {
        weatherUseCase.getCurrentWeatherData(city)
                .map { data -> mapper(data) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data -> mutableWeatherLiveData.value = data },
                        { error ->
                            Log.e(LOG_TAG, "exception during fetch data", error)
                            mutableWeatherErrorLiveData.value = (error.toString())
                        }
                ).also { compositeDisposable.add(it) }
    }

    fun fetchForecast(city: String, country: String, lat: String, lon: String) {
        weatherUseCase.getHourlyForecast2days(city, country, lat, lon)
                .map { data -> data.map { item -> mapper(item) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data -> mutableWeatherListLiveData.value = data },
                        { error ->
                            Log.e(LOG_TAG, "exception during fetch list data", error)
                            mutableWeatherErrorLiveData.value = (error.toString())
                        }
                ).also { compositeDisposable.add(it) }

    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}