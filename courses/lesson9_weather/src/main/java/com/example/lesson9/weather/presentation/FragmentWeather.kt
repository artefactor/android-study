package com.example.lesson9.weather.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.R
import com.example.lesson9.weather.databinding.DayBinding
import com.example.lesson9.weather.databinding.FragmentWeatherBinding
import com.example.lesson9.weather.datasource.network.OPEN_WEATHER_IMAGE_URL
import com.example.lesson9.weather.domain.WeatherDomainData
import com.google.android.material.snackbar.Snackbar


/**
 * both options works
 */
const val USE_PRESENTER = false

class FragmentWeather : Fragment(R.layout.fragment_weather), WeatherView {

    private val presenter = WeatherPresenterImpl(weatherView = this)
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var bindingDay1: DayBinding
    private lateinit var bindingDay2: DayBinding
    private lateinit var bindingDay3: DayBinding
    private lateinit var bindingDay4: DayBinding

    var viewModelFactory: ViewModelProvider.Factory = WeatherViewModelFactory()
    private lateinit var viewModel: WeatherViewModel
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "onViewCreated")

        binding = FragmentWeatherBinding.bind(view)
        bindingDay1 = DayBinding.bind(binding.dayInfo1[0])
        bindingDay2 = DayBinding.bind(binding.dayInfo2[0])
        bindingDay3 = DayBinding.bind(binding.dayInfo3[0])
        bindingDay4 = DayBinding.bind(binding.dayInfo4[0])

        if (!USE_PRESENTER) {
            viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)
            with(viewModel) {
                weatherLiveData.observe(viewLifecycleOwner, Observer { data -> showCurrentWeather(data) })
                weatherListLiveData.observe(viewLifecycleOwner, Observer { data -> showForecastList(data) })
                weatherErrorLiveData.observe(viewLifecycleOwner, Observer { error -> showError(error) })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (USE_PRESENTER) {
            presenter.fetchCurrentWeather("Minsk")
        } else {
            viewModel.fetchCurrentWeather("Minsk")
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.close()
    }

    override fun showCurrentWeather(data: WeatherDomainData) {
        Log.i(LOG_TAG, data.toString())
        if (USE_PRESENTER) {
            presenter.fetchForecast(data.lat, data.lon, data.city)
        }
        binding.bind(WeatherItemMapper().invoke(data))
        snackbar?.dismiss()
    }

    override fun showForecast(data: List<WeatherDomainData>) {
        Log.i(LOG_TAG, data.toString())
        val weatherItemMapper = WeatherItemMapper()
        bindingDay1.bind(weatherItemMapper.invoke(data[0]))
        bindingDay2.bind(weatherItemMapper.invoke(data[1]))
        bindingDay3.bind(weatherItemMapper.invoke(data[2]))
        bindingDay4.bind(weatherItemMapper.invoke(data[3]))
        snackbar?.dismiss()
    }

    private fun showCurrentWeather(data: WeatherItem) {
        Log.i(LOG_TAG, "Model:$data")
        viewModel.fetchForecast(data.city, data.lat, data.lon)
        binding.bind(data)
        snackbar?.dismiss()
    }

    private fun showForecastList(data: List<WeatherItem>) {
        Log.i(LOG_TAG, "Model:$data")
        bindingDay1.bind(data[0])
        bindingDay2.bind(data[1])
        bindingDay3.bind(data[2])
        bindingDay4.bind(data[3])
        snackbar?.dismiss()
    }

    private fun DayBinding.bind(item: WeatherItem) {
        date.text = item.date
        temp.text = item.temp
        description.text = item.title
        Glide.with(requireContext()).load("${OPEN_WEATHER_IMAGE_URL}${item.icon}.png").into(weatherIcon)
    }

    private fun FragmentWeatherBinding.bind(item: WeatherItem) {
        city.text = item.city
        date.text = item.date
        temp.text = item.temp
        description.text = item.title
        Glide.with(requireContext()).load("${OPEN_WEATHER_IMAGE_URL}${item.icon}.png").into(weatherIcon)
    }

    override fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
        snackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .also { it.show() }
    }


}