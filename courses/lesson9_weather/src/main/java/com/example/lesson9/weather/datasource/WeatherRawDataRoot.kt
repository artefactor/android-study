package com.example.lesson9.weather.datasource

import com.google.gson.annotations.SerializedName

/*
Еще я понимаю что можно было написать кастомный конвертер и вместо типов схожих со структурой JSOn
просто применить конвертер на этом этапе. Тогда бы никаких мапперов делать не надо было.
Данные бы на этапе парсинга разбирались уже сразу в нужном формате
И это было бы быстрее чем потом какой-то маппер дальше делать

 */
data class WeatherRawDataRoot(
        @SerializedName("name")
        val city: String,   //"London",
        val weather: List<WeatherRawData>,
        val main: WeatherRawDataMain,
        val dt: String,
        val coord: Coord,
)

data class Coord(
        val lon: String,
        val lat: String,
)


data class WeatherRawDataOneCallRoot(
        val daily: List<WeatherDailyRawData>,
        val lon: String,
        val lat: String,
)

data class WeatherRawDataMain(
        val temp: String,
)

data class WeatherDailyRawTemp(
        val day: String,
)

data class WeatherDailyRawData(
        val dt: String,
        val weather: List<WeatherRawData>,
        val temp: WeatherDailyRawTemp,
)

data class WeatherRawData(
        @SerializedName("main")
        val title: String,  //"Clouds"
        val description: String,  // "broken clouds"
        val icon: String, // "04d"
)

