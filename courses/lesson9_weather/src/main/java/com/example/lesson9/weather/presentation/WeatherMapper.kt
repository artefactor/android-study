package com.example.lesson9.weather.presentation

import android.util.Log
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import com.example.lesson9.weather.domain.WeatherDomainData
import java.text.SimpleDateFormat
import java.util.*

class WeatherMapper : (WeatherRawDataRoot) -> WeatherDomainData {
    override fun invoke(rawData: WeatherRawDataRoot): WeatherDomainData {
        Log.i(LOG_TAG, rawData.toString())
        return with(rawData) {
            WeatherDomainData(
                    city,
                    sys.country,
                    SimpleDateFormat.getDateInstance().format(Date(dt.toLong() * 1000)),
                    weather?.let { it[0]?.description },
                    main.temp,
                    weather?.let { it[0].icon },
                    coord.lon,
                    coord.lat,
            )
        }
    }
}

class WeatherListMapper : (WeatherRawDataOneCallRoot, String, String) -> List<WeatherDomainData> {
    override fun invoke(rawData: WeatherRawDataOneCallRoot, city: String, country: String): List<WeatherDomainData> {
        Log.i(LOG_TAG, rawData.toString())
        return rawData.daily.map { item ->
            with(item) {
                WeatherDomainData(
                        city,
                        country,
                        SimpleDateFormat.getDateInstance().format(Date(dt.toLong() * 1000)),
                        weather?.let { it[0]?.description },
                        temp.day,
                        weather?.let { it[0].icon },
                        rawData.lon,
                        rawData.lat,
                )
            }
        }
    }
}


