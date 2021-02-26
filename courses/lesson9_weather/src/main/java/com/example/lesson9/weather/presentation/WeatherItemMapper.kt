package com.example.lesson9.weather.presentation

import com.example.lesson9.weather.domain.WeatherDomainData
import kotlin.math.roundToInt

class WeatherItemMapper : (WeatherDomainData) -> WeatherItem {
    override fun invoke(data: WeatherDomainData): WeatherItem =
            with(data) {
                WeatherItem(
                        city,
                        date,
                        title,
                        "${temp.toFloat().roundToInt()}\u00B0",
                        icon,
                        lon,
                        lat
                )
            }
}