package com.example.lesson9.weather.presentation

interface WeatherFragmentManager {
    fun showWeatherFragment()
    fun showChangeCityFragment()
    fun showError(error:String )
    fun hideError()
    fun getStoredCityId(): Long
    fun storeCityId(itemId: Long)
}
