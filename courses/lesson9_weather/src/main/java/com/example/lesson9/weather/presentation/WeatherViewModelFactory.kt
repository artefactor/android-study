package com.example.lesson9.weather.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson9.weather.domain.WeatherUseCaseImpl
import io.reactivex.disposables.CompositeDisposable

class WeatherViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(
                    compositeDisposable = CompositeDisposable(),
                    weatherUseCase = WeatherUseCaseImpl(),
                    mapper = WeatherItemMapper()
            ) as T
        }
        throw IllegalArgumentException(
                "Unknown class fro the view model. Passed ${modelClass.canonicalName} " +
                        "but required NewsListViewModel"
        )
    }
}