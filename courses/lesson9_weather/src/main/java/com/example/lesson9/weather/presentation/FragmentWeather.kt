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
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * both options works
 */
const val USE_PRESENTER = false

class FragmentWeather : Fragment(R.layout.fragment_weather), WeatherView {

    private lateinit var presenter: WeatherPresenter
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var bindingDay1: DayBinding
    private lateinit var bindingDay2: DayBinding
    private lateinit var bindingDay3: DayBinding
    private lateinit var bindingDay4: DayBinding

    private lateinit var fragmentManager: WeatherFragmentManager
    var viewModelFactory: ViewModelProvider.Factory = WeatherViewModelFactory()
    private lateinit var viewModelWeather: WeatherViewModel
    private lateinit var viewModelCity: CitiesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentWeather#onViewCreated")

        presenter = WeatherPresenterImpl(requireActivity().applicationContext, weatherView = this)

        binding = FragmentWeatherBinding.bind(view)
        bindingDay1 = DayBinding.bind(binding.dayInfo1[0])
        bindingDay2 = DayBinding.bind(binding.dayInfo2[0])
        bindingDay3 = DayBinding.bind(binding.dayInfo3[0])
        bindingDay4 = DayBinding.bind(binding.dayInfo4[0])

        fragmentManager = requireActivity() as WeatherFragmentManager
        val selectedId: Long = fragmentManager.getStoredCityId()
        //  get city by id from ...

        binding.changeCity.setOnClickListener { fragmentManager.showChangeCityFragment() }

        viewModelCity = ViewModelProvider(this, viewModelFactory).get(CitiesViewModel::class.java)
        viewModelCity.init(requireActivity().applicationContext)
        with(viewModelCity) {
            cityLiveData.observe(viewLifecycleOwner, Observer { data -> fetchCurrentWeather(data.name) })
            errorLiveData.observe(viewLifecycleOwner, Observer { err -> showError(err) })
            fetchCity(selectedId)
        }

        if (!USE_PRESENTER) {
            viewModelWeather = ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)
            viewModelWeather.init(requireActivity().applicationContext)
            with(viewModelWeather) {
                weatherLiveData.observe(viewLifecycleOwner, Observer { data -> showCurrentWeather(data) })
                weatherListLiveData.observe(viewLifecycleOwner, Observer { data -> showForecastList(data) })
                weatherErrorLiveData.observe(viewLifecycleOwner, Observer { error -> showError(error) })
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        presenter.close()
    }

    private fun fetchCurrentWeather(cityName: String) {
        if (USE_PRESENTER) {
            presenter.fetchCurrentWeather(cityName)
        } else {
            viewModelWeather.fetchCurrentWeather(cityName)
        }
    }

    override fun showCurrentWeather(data: WeatherDomainData) {
        Log.i(LOG_TAG, data.toString())
        if (USE_PRESENTER) {
            presenter.fetchForecast(data.lat, data.lon, data.city, data.country)
        }
        binding.bind(WeatherItemMapper().invoke(data))
        hideError()
    }

    override fun showForecast(data: List<WeatherDomainData>) {
        Log.i(LOG_TAG, data.toString())
        val weatherItemMapper = WeatherItemMapper()
        bindingDay1.bind(weatherItemMapper.invoke(data[0]))
        bindingDay2.bind(weatherItemMapper.invoke(data[1]))
        bindingDay3.bind(weatherItemMapper.invoke(data[2]))
        bindingDay4.bind(weatherItemMapper.invoke(data[3]))
        hideError()
    }

    private fun showCurrentWeather(data: WeatherItem) {
        Log.i(LOG_TAG, "Model:$data")
        viewModelWeather.fetchForecast(data.city, data.country, data.lat, data.lon)
        /* здесь хотелось бы обновить данные в базе по городу, не уверен что правильно это делаю
           хочу запустить в отдельном потоке сохранение
         */
        updateCity(data)
        binding.bind(data)
        hideError()
    }

    private fun updateCity(data: WeatherItem) {
        Single.create<Long> {
            it.onSuccess(fragmentManager.getStoredCityId())
        }.subscribeOn(Schedulers.io())
                .map { id -> CityItem(id, data.city, data.country, data.lat, data.lon) }
                .map { cityItem -> viewModelCity.updateCity(cityItem) }
                .subscribe { result -> Log.i(LOG_TAG, result.toString()) }


    }

    private fun showForecastList(data: List<WeatherItem>) {
        Log.i(LOG_TAG, "Model:$data")
        bindingDay1.bind(data[0])
        bindingDay2.bind(data[1])
        bindingDay3.bind(data[2])
        bindingDay4.bind(data[3])
        hideError()
    }

    private fun DayBinding.bind(item: WeatherItem) {
        date.text = item.date
        temp.text = item.temp
        description.text = item.title
        //TODO Денис, может глайд тоже нужно в ио-потоке выполнять?
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
        fragmentManager.showError(errorMessage)
    }

    private fun hideError() {
        fragmentManager.hideError()
    }
}
