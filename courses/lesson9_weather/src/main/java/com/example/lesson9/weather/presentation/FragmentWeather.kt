package com.example.lesson9.weather.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.R
import com.example.lesson9.weather.domain.WeatherDomainData


class FragmentWeather : Fragment(R.layout.fragment_weather), WeatherView {

    private val presenter: WeatherPresenter = WeatherPresenterImpl(weatherView = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        presenter.fetchCurrentWeather("Minsk")
    }

    override fun onStop() {
        super.onStop()
        presenter.close()
    }

    override fun showCurrentWeather(data: WeatherDomainData) {
        Log.i(LOG_TAG, data.toString())
        presenter.fetchForecast(data.lat, data.lon, data.city)
    }

    override fun showForecast(data: List<WeatherDomainData>) {
        Log.i(LOG_TAG, data.toString())
    }

    override fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
    }


}